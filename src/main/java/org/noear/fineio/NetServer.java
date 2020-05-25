package org.noear.fine;

import org.noear.fine.nio.NioServer;

import java.io.IOException;

/**
 * 网络服务器
 * */
public abstract class NetServer<T> {
    /**
     * Nio net server
     * */
    public static <T> NetServer<T> nio(NetProtocol<T> protocol, NetProcessor<T> processor) {
        NetServer server = new NioServer<>();
        server.protocol = protocol;
        server.processor = processor;

        return server;
    }

    /**
     * 消息处理器
     * */
    protected NetProcessor<T> processor;
    protected NetProtocol<T> protocol;


    /**
     * 启动
     * */
    public abstract void start(int port) throws IOException;

    /**
     * 停止
     */
    public void stop() {
        _stop = true;
    }
    protected boolean _stop = false;
}
