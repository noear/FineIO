package org.noear.fineio.tcp;

import org.noear.fineio.core.Config;
import org.noear.fineio.core.NetSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioTcpAcceptor<T> {
    //缓冲
    private final ThreadLocal<ByteBuffer> thReadBuffer;
    //临时缓冲（用于转移半包内容）
    private final ThreadLocal<ByteBuffer> thReadBufferTmp;

    private final Config<T> config;
    private ExecutorService executors;


    public NioTcpAcceptor(Config<T> config, boolean pools) {
        this.config = config;

        thReadBuffer = ThreadLocal.withInitial(()-> ByteBuffer.allocateDirect(config.getBufferSize()));
        thReadBufferTmp = ThreadLocal.withInitial(()-> ByteBuffer.allocateDirect(config.getBufferSize()));

        if(pools){
            executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        }
    }

    public void accept(SelectionKey key, Selector selector) throws IOException{
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
            //sc.setOption(StandardSocketOptions.SO_KEEPALIVE, Boolean.TRUE);
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        }
    }

    public void read(SelectionKey key) throws IOException {
        if (executors == null) {
            read0(key);
        } else {
            executors.submit(() -> {
                try {
                    read0(key);
                }
                catch (ClosedChannelException ex){
                    key.cancel();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void read0(SelectionKey key) throws IOException{
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
                        }
                        catch (ClosedChannelException ex){
                            key.channel();
                            sc.close();
                        }
                        catch (Throwable ex) {
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
