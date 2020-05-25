package org.noear.fine;


import java.nio.ByteBuffer;

/**
 * 消息处理
 * */
public interface NetProcessor<T> {
    void process(NetSession<T> session);
}
