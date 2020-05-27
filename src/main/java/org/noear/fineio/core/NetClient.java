package org.noear.fineio.core;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 网络客户端
 * */
public class NetClient<T> {
    private ResourcePool<NetClientConnector<T>> pool;
    private final NetConfig<T> config;
    private final NetClientConnectorFactory<T> connectorFactory;

    public NetClient(Protocol<T> protocol, NetClientConnectorFactory<T> connectorFactory) {
        this.connectorFactory = connectorFactory;
        this.config = new NetConfig<>();
        this.config.setProtocol(protocol);

        this.pool = new ResourcePool<>(Runtime.getRuntime().availableProcessors(), new ResourceFactory<NetClientConnector<T>>() {
            @Override
            public NetClientConnector<T> create() throws Throwable {
                return connectorFactory.create(config);
            }

            @Override
            public NetClientConnector<T> check(NetClientConnector<T> connector) {
                return connectorFactory.check(connector);
            }

            @Override
            public NetClientConnector<T> close(NetClientConnector<T> connector) {
                return connectorFactory.close(connector);
            }
        });
    }

    public NetClient<T> bind(InetSocketAddress address) {
        config.setAddress(address);

        return this;
    }

    public NetClient<T> bind(String hostname, int port) {
        if (hostname == null) {
            return bind(new InetSocketAddress(port));
        } else {
            return bind(new InetSocketAddress(hostname, port));
        }
    }

    public NetClient<T> connectionTimeout(int seconds){
        config.setConnectionTimeout(seconds);
        return this;
    }

    /**
     * 接收
     * */
    public NetClient<T> receive(MessageProcessor<T> processor){
        config.setProcessor(processor);
        return this;
    }

    /**
     * 发送
     */
    public void send(T message) throws IOException {
        if(pool == null){
            return;
        }

        NetClientConnector<T> c = pool.apply();

        if(c != null && c.isValid()) {
            c.send(message);
            pool.free();
        }else{
            throw new IOException("Failed to get connection!");
        }
    }
}
