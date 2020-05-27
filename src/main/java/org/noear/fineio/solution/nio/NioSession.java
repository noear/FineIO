package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetSession;
import org.noear.fineio.core.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioSession<T> extends NetSession<T> {
    private SocketChannel _channel;
    private Protocol<T> _protocol;

    public NioSession(SocketChannel channel, Protocol<T> protocol){
        _channel = channel;
        _protocol = protocol;
    }

    /**
     * 写缓存
     * */
    @Override
    public void write(T message) throws IOException {
        if(isValid()) {
            ByteBuffer buf = _protocol.encode(message);
            _channel.write(buf);
        }
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
    public boolean isValid() {
        return _channel.isConnected();
    }

    @Override
    public void close() throws IOException {
        _channel.close();
    }

}
