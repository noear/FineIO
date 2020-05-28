package org.noear.fineio;

public class FineException extends RuntimeException {
    public FineException(String message){
        super(message);
    }

    public FineException(Throwable throwable){
        super(throwable);
    }
}
