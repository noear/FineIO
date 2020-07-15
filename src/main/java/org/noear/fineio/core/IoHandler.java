package org.noear.fineio.core;

public interface IoHandler<T> {
    void onConnect(NetConnector<T> connector);
    void onDisconnect(NetConnector<T> connector);
    void onRead(NetConnector<T> connector);
}
