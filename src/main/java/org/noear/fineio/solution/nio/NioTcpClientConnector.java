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

public class NioTcpClientConnector<T> extends NetClientConnector<T> {
    //缓冲
    private final ByteBuffer readBuffer;
    //临时缓冲（用于转移半包内容）
    private final ByteBuffer readBufferTmp;
    private CompletableFuture<Integer> connectionFuture;

    private Selector selector;
    private SocketChannel channel;


    public NioTcpClientConnector(NetConfig<T> config){
        super(config);
        this.connectionFuture = new CompletableFuture<>();
        this.readBuffer = ByteBuffer.allocateDirect(config.getBufferSize());
        this.readBufferTmp = ByteBuffer.allocateDirect(config.getBufferSize());
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

    private void selectDo(SelectionKey key) throws ClosedChannelException,IOException{
        if(key == null || key.isValid() == false){
            return;
        }

        SocketChannel sc = (SocketChannel) key.channel();

        if(key.isConnectable()){
            if (sc.finishConnect()) {
                sc.register(selector, SelectionKey.OP_READ);
                connectionFuture.complete(null);
            }else{
                this.colse();
            }
        }

        if(key.isReadable()){
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
                key.cancel();
                sc.close();
            }
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

    private void bufferClear(ByteBuffer buf) {
        buf.position(0);
        buf.limit(buf.capacity());
    }
}
