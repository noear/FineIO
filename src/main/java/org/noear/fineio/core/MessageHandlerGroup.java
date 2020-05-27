package org.noear.fineio.core;

import java.util.ArrayList;

/**
 * 消息处理组
 * */
public class MessageHandlerGroup<T> implements MessageHandler<T> {
    private ArrayList<MessageHandler<T>> group = new ArrayList<>();

    public void append(MessageHandler<T> processor) {
        group.add(processor);
    }

    @Override
    public void handle(NetSession<T> session, T message) throws Throwable{
        for (MessageHandler<T> processor : group) {
            processor.handle(session, message);
        }
    }
}
