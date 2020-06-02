package org.noear.fineio.core;

import java.net.InetSocketAddress;

public class IoConfig<T> {
    /**
     * 代理
     * */
    private MessageHandler<T> handler;
    /**
     * 协议
     * */
    private Protocol<T> protocol;
    /**
     * 地址
     * */
    private InetSocketAddress address;

    /**
     * 连接超时（单位：秒；默认1分钟）
     * */
    private int connectionTimeout = 60;

    /**
     * 读缓存大小（默认1024）
     * */
    private int readBufferSize = 1024;
    /**
     * 写缓存大小（默认512）
     * */
    private int writeBufferSize = 512;



    public MessageHandler<T> getHandler() {
        return handler;
    }
    public Protocol<T> getProtocol() {
        return protocol;
    }
    public InetSocketAddress getAddress() {
        return address;
    }
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    public int getReadBufferSize() {
        return readBufferSize;
    }
    public int getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setHandler(MessageHandler<T> handler) {
        this.handler = handler;
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
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }
    public void setWriteBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }
}
