package org.noear.fineio.core;

public interface Sender<T> {
    void send(T message);
}
