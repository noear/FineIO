package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetConfig;
import org.noear.fineio.core.NetSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NioTcpAcceptor<T> {
    //缓冲
    private final ByteBuffer readBuffer;
    //临时缓冲（用于转移半包内容）
    private final ByteBuffer readBufferTmp;

    private final NetConfig<T> config;


    public NioTcpAcceptor(NetConfig<T> config) {
        this.config = config;

        readBuffer = ByteBuffer.allocateDirect(config.getBufferSize());
        readBufferTmp = ByteBuffer.allocateDirect(config.getBufferSize());

    }

    public void receive(SelectionKey key) throws IOException{
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
                        if (readBuffer.hasRemaining()) {
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
