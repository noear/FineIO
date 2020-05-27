package org.noear.fineio.core;


/**
 * 消息处理
 * */
public interface MessageProcessor<T> {
    void process(NetSession<T> session, T message) throws Throwable;

    /**
     * 线程池模式
     * */
    default MessageProcessorPool<T> pools() {
        return new MessageProcessorPool<T>(this);
    }
}
