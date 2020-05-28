package org.noear.fineio.core;

public interface NetConnectorFactory<T>  {
    NetConnector<T> create(IoConfig<T> cfg) throws Throwable;

    default NetConnector<T> check(NetConnector<T> r){
        return r;
    }

    default NetConnector<T> free(NetConnector<T> r){
        return r;
    }

    default void close(NetConnector<T> r){ r.colse(); }
}
