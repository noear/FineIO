package org.noear.fineio.tcp;

import org.noear.fineio.core.IoConfig;
import org.noear.fineio.core.NetSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioTcpSession<T> extends NetSession<T> {
    private SocketChannel channel;
    private IoConfig<T> config;
    private NioWriteBuffer<T> writeBuffer;

    public NioTcpSession(SocketChannel channel, IoConfig<T> config){
        this.channel = channel;
        this.config = config;

        writeBuffer = new NioWriteBuffer<>(config,channel);
    }

    /**
     * 写缓存
     * */
    @Override
    public void write(T message) throws IOException {
        if(message == null){
            return;
        }

        if(isValid()) {
            writeBuffer.write(message);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException{
        return (InetSocketAddress)channel.getLocalAddress();
    }

    @Override
    public  InetSocketAddress getRemoteAddress() throws IOException{
        return (InetSocketAddress)channel.getRemoteAddress();
    }

    @Override
    public boolean isValid() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

}
