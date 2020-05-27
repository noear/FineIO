package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetServer;
import org.noear.fineio.core.NetSession;
import org.noear.fineio.core.Protocol;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioTcpServer<T> extends NetServer<T> {
    //缓冲
    private final ByteBuffer readBuffer;
    //临时缓冲（用于转移半包内容）
    private final ByteBuffer readBufferTmp;

    private Selector selector;

    public NioTcpServer(Protocol<T> protocol) {
        config.setProtocol(protocol);
        readBuffer = ByteBuffer.allocateDirect(config.getBufferSize());
        readBufferTmp = ByteBuffer.allocateDirect(config.getBufferSize());
    }

    /**
     * 开始
     */
    @Override
    public void start(boolean blocking) {
        if (blocking) {
            try {
                start0();
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } else {
            new Thread(() -> {
                start(true);
            }).start();
        }
    }

    private void start0() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(config.getAddress());

        selector = Selector.open();

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        startDo();
    }

    private void startDo() {
        while (!stopped) {
            try {
                if (selector.select(1000) < 1) {
                    continue;
                }

                Iterator<SelectionKey> keyS = selector.selectedKeys().iterator();

                while (keyS.hasNext()) {
                    SelectionKey key = keyS.next();
                    keyS.remove();

                    try {
                        selectDo(key);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
//                        if (key != null && key.channel() != null) {
//                            key.channel().close();
//                        }
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private void selectDo(SelectionKey key) throws IOException {
        if (key == null || key.isValid() == false) {
            return;
        }

        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            if (channel == null) {
                return;
            }

            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, Boolean.TRUE);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            return;
        }

        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            int size = -1;

            if (config.getProcessor() != null) {
                //
                //如果有处理器?
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
                            if(readBuffer.hasRemaining()) {
                                readBufferTmp.put(readBuffer);
                            }
                        } else {
                            //
                            //如果message没有问题，则执行处理
                            //
                            NetSession<T> session = new NioTcpSession<>(sc, config.getProtocol());

                            try {
                                config.getProcessor().process(session, message);
                            } catch (Throwable ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    //下次读之前，先清理一次
                    //
                    bufferClear(readBuffer);
                    if(readBufferTmp.hasRemaining()){
                        readBuffer.put(readBufferTmp);
                    }
                }
            }

            if (size < 0) {
                System.out.println("-- colse");
                key.cancel();
                sc.close();
            }
        }
    }

    private void bufferClear(ByteBuffer buf) {
        buf.position(0);
        buf.limit(buf.capacity());
    }
}
