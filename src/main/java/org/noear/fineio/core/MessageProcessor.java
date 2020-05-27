package org.noear.fineio.core;


/**
 * 消息处理
 * */
public interface MessageProcessor<T> {
    void process(NetSession<T> session, T message) throws Throwable;
}
