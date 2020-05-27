package org.noear.fineio.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandlerPool<T> implements MessageHandler<T> {
    private MessageHandler<T> processor;
    private ExecutorService executors;

    public MessageHandlerPool(MessageHandler<T> processor){
        this.processor = processor;
        this.executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    @Override
    public void handle(NetSession<T> session, T message) throws Throwable{
        executors.execute(()->{
            try {
                processor.handle(session, message);
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        });
    }

    @Override
    public MessageHandlerPool<T> pools() {
        return this;
    }
}
