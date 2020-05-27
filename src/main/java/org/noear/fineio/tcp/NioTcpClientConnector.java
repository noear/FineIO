package org.noear.fineio.tcp;

import org.noear.fineio.core.NetClientConnector;
import org.noear.fineio.core.Config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NioTcpClientConnector<T> extends NetClientConnector<T> {

    private CompletableFuture<Integer> connectionFuture;

    private Selector selector;
    private SocketChannel channel;
    private final NioTcpAcceptor<T> acceptor;


    public NioTcpClientConnector(Config<T> config){
        super(config);
        this.connectionFuture = new CompletableFuture<>();
        this.acceptor = new NioTcpAcceptor<>(config);
    }

    public NetClientConnector<T> connection() throws IOException {
        selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);

        //尝试连接
        if(channel.connect(config.getAddress())){
            channel.register(selector, SelectionKey.OP_READ);
            connectionFuture.complete(null);
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

    private void selectDo(SelectionKey key) throws IOException{
        if(key == null || key.isValid() == false){
            return;
        }

        if(key.isConnectable()){
            SocketChannel sc = (SocketChannel) key.channel();

            if (sc.finishConnect()) {
                sc.register(selector, SelectionKey.OP_READ);
                connectionFuture.complete(null);
            }else{
                this.colse();
            }
        }

        if(key.isReadable()){
            acceptor.receive(key);
        }
    }

    @Override
    public void send(T message) throws IOException {

        if (connectionFuture != null) {
            try {
                connectionFuture.get(config.getConnectionTimeout(), TimeUnit.SECONDS);
                connectionFuture = null;
            } catch (Exception ex) {
                throw new IOException("Connection timeout!");
            }

        }

        synchronized (channel) {
            ByteBuffer buf = config.getProtocol().encode(message);
            channel.write(buf);
        }
    }

    @Override
    public boolean isValid() {
        return channel.isOpen();
    }

    @Override
    public void colse() {
        try {
            colsed = true;
            channel.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    private boolean colsed;
}
