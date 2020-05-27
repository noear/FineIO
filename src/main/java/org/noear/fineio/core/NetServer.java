package org.noear.fineio.core;

import java.net.InetSocketAddress;

/**
 * 网络服务器
 * */
public abstract class NetServer<T> {

    /**
     * 配置
     * */
    protected final NetConfig<T> config = new NetConfig<>();

    public NetServer<T> bind(InetSocketAddress address) {
        config.setAddress(address);
        return this;
    }

    public NetServer<T> bind(String hostname, int port) {
        config.setAddress(hostname, port);
        return this;
    }

    public NetServer<T> process(MessageProcessor<T> processor){
        config.setProcessor(processor);
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
