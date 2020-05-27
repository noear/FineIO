package org.noear.fineio.core;

import java.net.InetSocketAddress;

/**
 * 网络服务器
 * */
public abstract class NetServer<T> {

    /**
     * 配置
     * */
    protected final Config<T> config;

    public NetServer(Config<T> config){
        this.config = config;
    }

    public NetServer<T> bind(InetSocketAddress address) {
        config.setAddress(address);
        return this;
    }

    public NetServer<T> bind(String hostname, int port) {
        config.setAddress(hostname, port);
        return this;
    }

    public NetServer<T> handle(MessageHandler<T> handler){
        config.setHandler(handler);
        return this;
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
