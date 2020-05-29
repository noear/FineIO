package org.noear.fineio.core;

import org.noear.fineio.FineException;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 网络客户端
 * */
public class NetClient<T> implements Sender<T>{
    private ResourcePool<NetConnector<T>> connectors;
    private final IoConfig<T> config;

    public NetClient(Protocol<T> protocol, NetConnectorFactory<T> connectorFactory) {
        this(protocol, new IoConfig<T>(), connectorFactory);
    }

    public NetClient(Protocol<T> protocol, IoConfig<T> cfg, NetConnectorFactory<T> connectorFactory) {
        this.config = cfg;
        this.config.setProtocol(protocol);

        this.connectors = new ResourcePool<>(Runtime.getRuntime().availableProcessors() * 2 + 1, new ResourceFactory<NetConnector<T>>() {
            @Override
            public NetConnector<T> create() throws Throwable {
                return connectorFactory.create(config);
            }

            @Override
            public NetConnector<T> check(NetConnector<T> connector) {
                return connectorFactory.check(connector);
            }

            @Override
            public NetConnector<T> free(NetConnector<T> connector) {
                return connectorFactory.free(connector);
            }

            @Override
            public void close(NetConnector<T> connector) {
                connectorFactory.close(connector);
            }
        });
    }

    public IoConfig<T> config() {
        return this.config;
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

    public NetClient<T> connectionTimeout(int seconds) {
        config.setConnectionTimeout(seconds);
        return this;
    }

    /**
     * 接收
     */
    public NetClient<T> handle(MessageHandler<T> handler) {
        config.setHandler(handler);
        return this;
    }

    /**
     * 发送
     */
    public void send(T message) {
        NetConnector<T> c = connectors.apply();

        if (c != null) {
            try {
                c.send(message);
            } finally {
                connectors.free();
            }
        } else {
            throw new FineException("Failed to get connection!");
        }
    }

    /**
     * 获取一个连接
     */
    public NetConnector<T> getConnector() {
        return connectors.apply();
    }

    /**
     * 关闭客户端
     */
    public void colse() {
        connectors.clear();
    }
}
