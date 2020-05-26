package org.noear.fineio.nio;

import org.noear.fineio.NetClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class NioClient<T> extends NetClient<T> {
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private Selector selector;
    private CompletableFuture<Integer> sendFuture;
    private SocketChannel channel;

    public NioClient(){
        sendFuture = new CompletableFuture<>();
    }

    @Override
    public void connection(InetSocketAddress address) throws IOException {
        selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);

        if(channel.connect(address)){
            channel.register(selector, SelectionKey.OP_READ);
            sendFuture.complete(null);
        }else {
            channel.register(selector, SelectionKey.OP_CONNECT);
        }

        startDo();
    }


    private void startDo(){
        while (!colsed){
            try{
                selector.select();

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

            }catch (Throwable ex){
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

    private void selectDo(SelectionKey key) throws ClosedChannelException,IOException{
        if(key == null || key.isValid() == false){
            return;
        }

        SocketChannel sc = (SocketChannel) key.channel();

        if(key.isConnectable()){
            if (sc.finishConnect()) {
                sc.register(selector, SelectionKey.OP_READ);
                sendFuture.complete(null);
            }else{
                this.colse();
            }
        }

        if(key.isReadable()){
            buffer.clear();
            int size = sc.read(buffer);

            if (size > 0) {
                buffer.flip();

                T message = protocol.decode(buffer);

                if (message != null) {
                    //
                    //如果message没有问题，则执行处理
                    //
                    NioSession<T> session = new NioSession<>(sc, message);

                    processor.process(session);
                }
            }

            if (size < 0) {
                key.cancel();
                sc.close();
            }
        }
    }

    @Override
    public void send(ByteBuffer buffer) throws IOException{
        if(sendFuture != null) {
            try {
                sendFuture.get();
                sendFuture = null;
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

        synchronized (channel) {
            while(buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
    }
}
