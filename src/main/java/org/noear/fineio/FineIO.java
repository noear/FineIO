package org.noear.fineio;

import org.noear.fineio.core.*;
import org.noear.fineio.solution.nio.NioTcpClientConnector;
import org.noear.fineio.solution.nio.NioTcpServer;

public final class FineIO {
    public static <T> NetClient<T> client(IoProtocol<T> protocol) {
        return client(protocol, new IoConfig<T>());
    }

    public static <T> NetClient<T> client(IoProtocol<T> protocol, IoConfig<T> cfg) {
        return new NetClient(protocol, cfg, new NetClientConnectorFactory<T>() {
            @Override
            public NetClientConnector<T> create(IoConfig cfg) throws Throwable {
                return new NioTcpClientConnector<T>(cfg).connection();
            }

            @Override
            public NetClientConnector<T> check(NetClientConnector<T> r) {
                if (r.isValid()) {
                    return r;
                } else {
                    r.colse();
                    return null;
                }
            }
        });
    }

    public static <T> NetServer<T> server(IoProtocol<T> protocol) {
        return server(protocol, new IoConfig<T>());
    }

    public static <T> NetServer<T> server(IoProtocol<T> protocol, IoConfig<T> cfg) {
        return new NioTcpServer<T>(protocol, cfg);
    }
}
