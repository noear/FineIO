package org.noear.fineio.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandlerPools<T> implements MessageHandler<T> {
    private MessageHandler<T> handler;
    private ExecutorService executors;

    public MessageHandlerPools(MessageHandler<T> handler){
        this.handler = handler;
        this.executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    @Override
    public void handle(NetSession<T> session, T message) throws Throwable{
        executors.execute(()->{
            try {
                handler.handle(session, message);
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        });
    }

    @Override
    public MessageHandlerPools<T> pools() {
        return this;
    }
}
