package org.noear.fineio.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 网络会话
 * */
public abstract class NetSession<T> {

    /**
     * 写缓存
     * */
    public abstract void write(T message) throws IOException;

    public abstract InetSocketAddress getLocalAddress() throws IOException;

    public abstract InetSocketAddress getRemoteAddress() throws IOException;

    public abstract boolean isOpen();

    public abstract void close() throws IOException;

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
