package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetClientConnector;
import org.noear.fineio.core.NetConfig;
import org.noear.fineio.core.NetSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NioClientConnector<T> extends NetClientConnector<T> {
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;
    private CompletableFuture<Integer> sendFuture;
    private SocketChannel channel;


    public NioClientConnector(NetConfig<T> config){
        super(config);
        this.sendFuture = new CompletableFuture<>();
    }

    public NetClientConnector<T> connection() throws IOException {
        selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);

        //尝试连接
        if(channel.connect(config.getAddress())){
            channel.register(selector, SelectionKey.OP_READ);
            sendFuture.complete(null);
        }else {
            channel.register(selector, SelectionKey.OP_CONNECT);
        }

        new Thread(this::startDo).start();

        return this;
    }


    private void startDo(){
        while (!colsed){
            try{
                if(selector.select(1000) < 1){
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
            int size = -1;

            if(config.getProcessor() != null) {
                //
                //如果有处理器?
                //
                bufferClear();
                size = sc.read(buffer);

                if (size > 0) {
                    buffer.flip();

                    T message = config.getProtocol().request(buffer);

                    if (message != null) {
                        //
                        //如果message没有问题，则执行处理
                        //
                        NetSession session = new NetSession(sc);

                        config.getProcessor().process(session, message);
                    }
                }
            }

            if (size < 0) {
                key.cancel();
                sc.close();
            }
        }
    }

    @Override
    public void send(T message) throws IOException {
        ByteBuffer buffer = config.getProtocol().encode(message);

        if (sendFuture != null) {
            try {
                //3秒，则算超时
                //
                sendFuture.get(3, TimeUnit.SECONDS);
                sendFuture = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        channel.write(buffer);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    private void bufferClear(){
        buffer.position(0);
        buffer.limit(buffer.capacity());
    }
}
