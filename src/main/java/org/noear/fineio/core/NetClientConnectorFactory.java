package org.noear.fineio.core;

public interface NetClientConnectorFactory<T>  {
    NetClientConnector<T> create(NetConfig<T> cfg) throws Throwable;

    default NetClientConnector<T> open(NetClientConnector<T> r){
        return r;
    }

    default NetClientConnector<T> close(NetClientConnector<T> r){
        return r;
    }
}
