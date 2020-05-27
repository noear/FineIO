package org.noear.fineio.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 网络会话
 * */
public class NetSession {
    private SocketChannel _channel;

    public NetSession(SocketChannel channel){
        _channel = channel;
    }

    /**
     * 写缓存
     * */
    public void write(ByteBuffer buf) throws IOException{
        _channel.write(buf);
    }

    public void write(byte[] bytes) throws IOException{
        ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
        buf.put(bytes);
        buf.flip();
        write(buf);
    }

    public  InetSocketAddress getLocalAddress() throws IOException{
        return (InetSocketAddress)_channel.getLocalAddress();
    }

    public  InetSocketAddress getRemoteAddress() throws IOException{
        return (InetSocketAddress)_channel.getRemoteAddress();
    }

    public boolean isOpen() {
        return _channel.isOpen();
    }

    public void close() throws IOException {
        _channel.close();
    }

    private Object _attachment;
    /**
     * 附件
     * */
    public Object attachment(){
        return _attachment;
    }

    /**
     * 设置附件
     * */
    public void attachmentSet(Object obj){
        _attachment = obj;
    }
}
