package org.noear.fineio.core;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 网络客户端
 * */
public class NetClient<T> {
    private ResourcePool<NetClientConnector<T>> pool;
    private final IoConfig<T> config;

    public NetClient(IoProtocol<T> protocol, NetClientConnectorFactory<T> connectorFactory) {
        this(protocol, new IoConfig<T>(), connectorFactory);
    }

    public NetClient(IoProtocol<T> protocol, IoConfig<T> cfg, NetClientConnectorFactory<T> connectorFactory) {
        this.config = cfg;
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
            public NetClientConnector<T> free(NetClientConnector<T> connector) {
                return connectorFactory.free(connector);
            }

            @Override
            public void close(NetClientConnector<T> connector) {
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
    public void send(T message) throws IOException {
        if (pool == null) {
            return;
        }

        NetClientConnector<T> c = pool.apply();

        if (c != null) {
            try {
                c.send(message);
            } finally {
                pool.free();
            }
        } else {
            throw new IOException("Failed to get connection!");
        }
    }

    /**
     * 获取一个连接
     */
    public NetClientConnector<T> getConnector() {
        return pool.apply();
    }

    /**
     * 关闭客户端
     */
    public void colse() {
        pool.clear();
    }
}
