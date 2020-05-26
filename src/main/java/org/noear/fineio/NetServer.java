package org.noear.fineio;

import org.noear.fineio.nio.NioServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 网络服务器
 * */
public abstract class NetServer<T> {
    /**
     * Nio net server
     * */
    public static <T> NetServer<T> nio(Protocol<T> protocol, SessionProcessor<T> processor) {
        NetServer<T> server = new NioServer<T>();
        server.protocol = protocol;
        server.processor = processor;

        return server;
    }

    /**
     * 消息处理器
     * */
    protected SessionProcessor<T> processor;
    protected Protocol<T> protocol;
    protected InetSocketAddress address;

    public NetServer<T> bind(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public NetServer<T> bind(String hostname, int port) {
        if (hostname == null) {
            return bind(new InetSocketAddress(port));
        } else {
            return bind(new InetSocketAddress(hostname, port));
        }
    }

    /**
     * 启动
     * */
    public abstract void start(boolean blocking);

    /**
     * 停止
     */
    public void stop() {
        stopped = true;
    }
    protected boolean stopped = false;
}
