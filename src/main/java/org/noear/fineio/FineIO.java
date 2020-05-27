package org.noear.fineio;

import org.noear.fineio.core.*;
import org.noear.fineio.nio.NioClientConnector;
import org.noear.fineio.nio.NioServer;

public final class FineIO {
    public static <T> NetClient<T> client(Protocol<T> protocol) {
        return new NetClient(protocol, new NetClientConnectorFactory() {
            @Override
            public NetClientConnector create(NetConfig cfg) throws Throwable {
                return new NioClientConnector<T>(cfg).connection();
            }
        });
    }

    public static <T> NetServer<T> server(Protocol<T> protocol) {
        return new NioServer<T>(protocol);
    }
}
