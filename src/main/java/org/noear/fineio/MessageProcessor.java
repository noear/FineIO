package org.noear.fineio;


/**
 * 会话处理
 * */
public interface MessageProcessor<T> {
    void process(NetSession<T> session);
}
