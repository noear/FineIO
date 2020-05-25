package org.noear.fine.extension;

import org.noear.fine.NetProcessor;
import org.noear.fine.NetSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessorPool<T> implements NetProcessor<T> {
    private NetProcessor<T> processor;
    private ExecutorService processorPool;

    public MessageProcessorPool(NetProcessor<T> processor){
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
