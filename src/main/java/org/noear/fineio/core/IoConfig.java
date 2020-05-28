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
     * 缓存大小（默认1024）
     * */
    private int bufferSize = 1024;



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

    public int getBufferSize() {
        return bufferSize;
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

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
