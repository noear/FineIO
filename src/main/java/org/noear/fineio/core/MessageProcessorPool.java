package org.noear.fineio.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessorPool<T> implements MessageProcessor<T> {
    private MessageProcessor<T> processor;
    private ExecutorService executors;

    public MessageProcessorPool(MessageProcessor<T> processor){
        this.processor = processor;
        this.executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    @Override
    public void process(NetSession<T> session, T message) throws Throwable{
        executors.execute(()->{
            try {
                processor.process(session, message);
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        });
    }
}
