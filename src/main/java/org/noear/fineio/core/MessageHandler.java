package org.noear.fineio.core;


/**
 * 消息处理
 * */
public interface MessageHandler<T> {
    void handle(NetSession<T> session, T message) throws Throwable;

    /**
     * 线程池模式
     * */
    default MessageHandlerPool<T> pools() {
        return new MessageHandlerPool<T>(this);
    }
}
