package org.noear.fine.extension;

import org.noear.fine.NetProcessor;
import org.noear.fine.NetSession;

import java.util.ArrayList;

/**
 * 消息处理组
 * */
public class MessageProcessorGroup<T> implements NetProcessor<T> {
    private ArrayList<NetProcessor<T>> group = new ArrayList<>();

    public void append(NetProcessor<T> processor) {
        group.add(processor);
    }

    @Override
    public void process(NetSession<T> session) {
        for (NetProcessor<T> processor : group) {
            processor.process(session);
        }
    }
}
