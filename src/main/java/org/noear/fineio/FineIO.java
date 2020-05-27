package org.noear.fineio;

import org.noear.fineio.core.*;
import org.noear.fineio.solution.nio.NioClientConnector;
import org.noear.fineio.solution.nio.NioServer;

public final class FineIO {
    public static <T> NetClient<T> client(Protocol<T> protocol) {
        return new NetClient(protocol, new NetClientConnectorFactory<T>() {
            @Override
            public NetClientConnector<T> create(NetConfig cfg) throws Throwable {
                return new NioClientConnector<T>(cfg).connection();
            }

            @Override
            public NetClientConnector<T> check(NetClientConnector<T> r) {
                if (r.isOpen()) {
                    return r;
                } else {
                    r.colse();
                    return null;
                }
            }
        });
    }

    public static <T> NetServer<T> server(Protocol<T> protocol) {
        return new NioServer<T>(protocol);
    }
}
