package org.noear.fineio.core;

import java.io.IOException;

public abstract class NetConnector<T> {
    /**
     * 配置
     */
    protected final Config<T> config;

    /**
     * 构建函数
     * */
    public NetConnector(Config<T> config){
        this.config = config;
    }


    /**
     * 连接
     */
    public abstract NetConnector<T> connection() throws IOException;

    /**
     * 发送
     */
    public abstract void send(T message) throws IOException;


    /**
     * 是否已打开
     * */
    public abstract boolean isValid();

    /**
     * 关闭
     */
    public abstract void colse();
}
