package org.noear.fineio.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessorPool<T> implements MessageProcessor<T> {
    private MessageProcessor<T> processor;
    private ExecutorService processorPool;

    public MessageProcessorPool(MessageProcessor<T> processor){
        this.processor = processor;
        this.processorPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    @Override
    public void process(NetSession session, T message) {
        processorPool.execute(()->{
            processor.process(session, message);
        });
    }
}
