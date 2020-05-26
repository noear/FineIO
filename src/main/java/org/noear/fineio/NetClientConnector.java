package org.noear.fineio;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class NetClientConnector<T> {
    /**
     * 消息处理器
     */
    protected SessionProcessor<T> processor;
    protected Protocol<T> protocol;
    protected InetSocketAddress address;

    public void setProcessor(SessionProcessor<T> processor) {
        this.processor = processor;
    }

    public void setProtocol(Protocol<T> protocol) {
        this.protocol = protocol;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public void setAddress(String hostname, int port) {
        if (hostname == null) {
            setAddress(new InetSocketAddress(port));
        } else {
            setAddress(new InetSocketAddress(hostname, port));
        }
    }

    /**
     * 连接
     */
    public abstract void connection() throws IOException;

    /**
     * 发送
     */
    public abstract void send(T message) throws IOException;

    /**
     * 关闭
     */
    public void colse() {
        colsed = true;
    }

    protected boolean colsed = false;
}
