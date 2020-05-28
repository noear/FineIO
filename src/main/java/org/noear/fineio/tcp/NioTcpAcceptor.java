package org.noear.fineio.tcp;

import org.noear.fineio.core.Config;
import org.noear.fineio.core.NetSession;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioTcpAcceptor<T> {
    private static final String err_broken_pipe ="Broken pipe";
    private static final String err_protocol_wrong = "Protocol wrong type for socket";
    //缓冲
    private final ThreadLocal<ByteBuffer> thReadBuffer;
    //临时缓冲（用于转移半包内容）
    private final ThreadLocal<ByteBuffer> thReadBufferTmp;

    private final Config<T> config;
    private ExecutorService executors;


    public NioTcpAcceptor(Config<T> config, boolean pools) {
        this.config = config;

        thReadBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(config.getBufferSize()));
        thReadBufferTmp = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(config.getBufferSize()));

        if (pools) {
            executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        }
    }

    public void accept(SelectionKey key, Selector selector) throws IOException {
        if (executors == null) {
            accept0(key, selector);
        } else {
            executors.submit(() -> {
                try {
                    accept0(key, selector);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void accept0(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel sc = server.accept();

        if (sc != null) {
            sc.setOption(StandardSocketOptions.SO_KEEPALIVE, Boolean.TRUE);
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    public void read(SelectionKey key) {
        if (executors == null) {
            read1(key);
        } else {
            executors.submit(() -> {
                read1(key);
            });
        }
    }

    private void read1(SelectionKey key) {
        try {
            read0(key);
        } catch (ClosedChannelException ex) {
            close0(key);
        } catch (IOException ex) {
            if(err_broken_pipe.equals(ex.getMessage()) || err_protocol_wrong.equals(ex.getMessage())) {
                close0(key);
            }else{
                ex.printStackTrace();
            }
        }
    }

    private void close0(SelectionKey key) {
        if (key == null) {
            return;
        }

        if (key.channel() != null) {
            try {
                key.channel().close();
            } catch (Exception ex2) {
            }
        }

        key.cancel();
    }

    private void read0(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();

        int size = -1;

        if (config.getHandler() != null) {

            ByteBuffer readBuffer = thReadBuffer.get();
            ByteBuffer readBufferTmp = thReadBufferTmp.get();

            //
            //如果有代理?
            //
            bufferClear(readBuffer);

            while ((size = sc.read(readBuffer)) > 0) {
                readBuffer.flip();

                //清空临时缓冲；准备接收半包
                bufferClear(readBufferTmp);

                while (readBuffer.hasRemaining()) {
                    //尝试多次解码
                    //
                    readBuffer.mark();
                    T message = config.getProtocol().decode(readBuffer);

                    if (message == null) {
                        readBuffer.reset();

                        //把留下的半包转到临时缓冲
                        if (readBuffer.hasRemaining()) {
                            readBufferTmp.put(readBuffer);
                        }
                    } else {
                        //
                        //如果message没有问题，则执行处理
                        //
                        NetSession<T> session = new NioTcpSession<>(sc, config.getProtocol());

                        try {
                            config.getHandler().handle(session, message);
                        } catch (ClosedChannelException ex) {
                            throw ex;
                        } catch (IOException ex) {
                            throw ex;
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                //下次读之前，先清理一次
                //
                bufferClear(readBuffer);
                if (readBufferTmp.hasRemaining()) {
                    readBuffer.put(readBufferTmp);
                }
            }
        }

        if (size < 0) {
            key.cancel();
            sc.close();
        }
    }

    private void bufferClear(ByteBuffer buf) {
        buf.position(0);
        buf.limit(buf.capacity());
        buf.mark();
    }
}
