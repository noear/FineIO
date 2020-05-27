package org.noear.fineio.core;

import java.io.IOException;

public abstract class NetClientConnector<T> {
    /**
     * 消息处理器
     */
    protected final NetConfig<T> config;

    /**
     * 构建函数
     * */
    public NetClientConnector(NetConfig<T> config){
        this.config = config;
    }


    /**
     * 连接
     */
    public abstract NetClientConnector<T> connection() throws IOException;

    /**
     * 发送
     */
    public abstract void send(T message) throws IOException;


    /**
     * 是否已打开
     * */
    public abstract boolean isOpen();

    /**
     * 关闭
     */
    public void colse() {
        colsed = true;
    }

    protected boolean colsed = false;
}
