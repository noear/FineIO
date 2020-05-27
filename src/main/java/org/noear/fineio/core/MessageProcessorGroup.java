package org.noear.fineio.core;

import java.util.ArrayList;

/**
 * 消息处理组
 * */
public class MessageProcessorGroup<T> implements MessageProcessor<T> {
    private ArrayList<MessageProcessor<T>> group = new ArrayList<>();

    public void append(MessageProcessor<T> processor) {
        group.add(processor);
    }

    @Override
    public void process(NetSession<T> session, T message) {
        for (MessageProcessor<T> processor : group) {
            processor.process(session, message);
        }
    }
}
