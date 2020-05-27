package org.noear.fineio;

import org.noear.fineio.core.*;
import org.noear.fineio.solution.nio.NioTcpClientConnector;
import org.noear.fineio.solution.nio.NioTcpServer;

public final class FineIO {
    public static <T> NetClient<T> client(Protocol<T> protocol) {
        return new NetClient(protocol, new NetClientConnectorFactory<T>() {
            @Override
            public NetClientConnector<T> create(NetConfig cfg) throws Throwable {
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

    public static <T> NetServer<T> server(Protocol<T> protocol) {
        return new NioTcpServer<T>(protocol);
    }
}
