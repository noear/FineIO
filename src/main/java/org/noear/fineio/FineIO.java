package org.noear.fineio;

import org.noear.fineio.core.*;
import org.noear.fineio.tcp.NioTcpClientConnector;
import org.noear.fineio.tcp.NioTcpServer;

public final class FineIO {
    public static <T> NetClient<T> client(Protocol<T> protocol) {
        return client(protocol, new Config<T>());
    }

    public static <T> NetClient<T> client(Protocol<T> protocol, Config<T> cfg) {
        return new NetClient(protocol, cfg, new NetConnectorFactory<T>() {
            @Override
            public NetConnector<T> create(Config cfg) throws Throwable {
                return new NioTcpClientConnector<T>(cfg).connection();
            }

            @Override
            public NetConnector<T> check(NetConnector<T> r) {
                if (r.isValid()) {
                    return r;
                } else {
                    r.colse();
                    return null;
                }
            }
        });
    }

    public static <T> NetServer<T> server(Protocol<T> protocol) {
        return server(protocol, new Config<T>());
    }

    public static <T> NetServer<T> server(Protocol<T> protocol, Config<T> cfg) {
        return new NioTcpServer<T>(protocol, cfg);
    }
}
