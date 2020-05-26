package org.noear.fineio.extension;

import org.noear.fineio.MessageProcessor;
import org.noear.fineio.NetSession;

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
    public void process(NetSession<T> session) {
        processorPool.execute(()->{
            processor.process(session);
        });
    }
}
