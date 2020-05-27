package org.noear.fineio.core;

public interface NetClientConnectorFactory<T>  {
    NetClientConnector<T> create(IoConfig<T> cfg) throws Throwable;

    default NetClientConnector<T> check(NetClientConnector<T> r){
        return r;
    }

    default NetClientConnector<T> free(NetClientConnector<T> r){
        return r;
    }

    default void close(NetClientConnector<T> r){ r.colse(); }
}
