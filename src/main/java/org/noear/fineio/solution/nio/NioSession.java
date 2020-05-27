package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioSession extends NetSession {
    private SocketChannel _channel;

    public NioSession(SocketChannel channel){
        _channel = channel;
    }

    /**
     * 写缓存
     * */
    @Override
    public void write(ByteBuffer buf) throws IOException {
        _channel.write(buf);
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException{
        return (InetSocketAddress)_channel.getLocalAddress();
    }

    @Override
    public  InetSocketAddress getRemoteAddress() throws IOException{
        return (InetSocketAddress)_channel.getRemoteAddress();
    }

    @Override
    public boolean isOpen() {
        return _channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        _channel.close();
    }

}
