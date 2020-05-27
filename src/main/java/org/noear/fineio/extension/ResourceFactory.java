package org.noear.fineio.extension;

public interface ResourceFactory<R> {
    R create() throws Throwable;

    default R open(R r){
        return r;
    }

    default R close(R r){
        return r;
    }
}
