package org.noear.fineio;

public class FineAIO {
    public static <T> NetClient<T> client(Protocol<T> protocol, SessionProcessor<T> processor) {
        return NetClient.nio(protocol, processor);
    }

    public static <T> NetServer<T> server(Protocol<T> protocol, SessionProcessor<T> processor) {
        return NetServer.nio(protocol, processor);
    }
}
