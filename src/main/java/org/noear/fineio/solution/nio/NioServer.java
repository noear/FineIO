package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetServer;
import org.noear.fineio.core.NetSession;
import org.noear.fineio.core.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer<T> extends NetServer<T> {
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;

    public NioServer(Protocol<T> protocol){
        config.setProtocol(protocol);
    }

    /**
     * 开始
     * */
    @Override
    public void start(boolean blocking) {
        if(blocking){
            try {
                start0();
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }else{
            new Thread(() -> {
                start(true);
            }).start();
        }
    }

    private void start0() throws IOException {
        ServerSocketChannel ssc =  ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(config.getAddress());

        selector = Selector.open();

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        startDo();
    }

    private void startDo() {
        while (!stopped) {
            try {
                if(selector.select(1000) <1){
                    continue;
                }

                Iterator<SelectionKey> keyS = selector.selectedKeys().iterator();

                while (keyS.hasNext()) {
                    SelectionKey key = keyS.next();
                    keyS.remove();

                    try {
                        selectDo(key);
                    }catch (Throwable ex) {
                        if (key != null && key.channel() != null) {
                            key.channel().close();
                        }
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        if(selector != null){
            try {
                selector.close();
            }catch (Throwable ex){
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

            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            return;
        }

        if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            int size = -1;

            if (config.getProcessor() != null) {
                //
                //如果有处理器?
                //
                bufferClear();
                size = channel.read(buffer);

                if (size > 0) {
                    buffer.flip();

                    T message = config.getProtocol().request(buffer);

                    if (message != null) {
                        //
                        //如果message没有问题，则执行处理
                        //
                        NetSession session = new NetSession(channel);

                        config.getProcessor().process(session, message);
                    }
                }
            }

            if (size < 0) {
                key.cancel();
                channel.close();
            }
        }
    }

    private void bufferClear(){
        buffer.position(0);
        buffer.limit(buffer.capacity());
    }
}
