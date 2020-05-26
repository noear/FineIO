package org.noear.fineio;

public final class FineIO {
    public static <T> NetClient<T> client(Protocol<T> protocol, SessionProcessor<T> processor, String hostname, int port)
    {
        return NetClient.nio(protocol,processor,hostname,port);
    }

    public static <T> NetServer<T> server(Protocol<T> protocol, SessionProcessor<T> processor) {
        return NetServer.nio(protocol,processor);
    }
}
