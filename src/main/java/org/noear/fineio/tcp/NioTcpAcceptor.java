package org.noear.fineio.tcp;

import org.noear.fineio.core.IoConfig;
import org.noear.fineio.core.NetSession;
import org.noear.fineio.core.IoRunner;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioTcpAcceptor<T> {
    //缓冲
    private final ThreadLocal<ByteBuffer> thReadBuffer;

    private final IoConfig<T> config;
    private ExecutorService executors;


    public NioTcpAcceptor(IoConfig<T> config, boolean pools) {
        this.config = config;

        thReadBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(config.getBufferSize()));

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
            readDo(key);
        } else {
            executors.submit(() -> {
                readDo(key);
            });
        }
    }

    private void readDo(SelectionKey key) {
        IoRunner.run(() -> {
            read0(key);
        }, () -> {
            close0(key);
        });
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

            //
            //如果有代理?
            //
            bufferClear(readBuffer);

            while ((size = sc.read(readBuffer)) > 0) {
                read1(sc, readBuffer);
            }
        }

        if (size < 0) {
            key.cancel();
            sc.close();
        }
    }

    private void read1(SocketChannel sc, ByteBuffer readBuffer) throws IOException {
        while (readBuffer.hasRemaining()) {
            readBuffer.flip();

            //尝试多次解码
            //
            T message = config.getProtocol().decode(readBuffer);

            if (message != null) {
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

        //数据读取完毕
        if (readBuffer.remaining() == 0) {
            bufferClear(readBuffer);
        } else if (readBuffer.position() > 0) {
            //半包，移位，接着读
            readBuffer.compact();
        } else {
            readBuffer.position(readBuffer.limit());
            readBuffer.limit(readBuffer.capacity());
        }

        //读缓冲区已满
        if (!readBuffer.hasRemaining()) {
            throw new RuntimeException("ReadBuffer overflow");
        }
    }

    private void bufferClear(ByteBuffer buf) {
        buf.position(0);
        buf.limit(buf.capacity());
        buf.mark();
    }
}
