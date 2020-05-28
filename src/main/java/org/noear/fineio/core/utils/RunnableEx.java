package org.noear.fineio.core.utils;

public interface RunnableEx<E extends Throwable> {
    void run() throws E;
}
