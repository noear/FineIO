package org.noear.fineio;

import org.noear.fineio.extension.ResourcePool;
import org.noear.fineio.nio.NioClientConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 网络客户端
 * */
public class NetClient<T> {

    private ResourcePool<NioClientConnector<T>> pool;

    private SessionProcessor<T> processor;
    private Protocol<T> protocol;
    private InetSocketAddress address;

    /**
     * Nio net server
     */
    public static <T> NetClient<T> nio(Protocol<T> protocol, SessionProcessor<T> processor, String hostname, int port) {
        NetClient<T> client = new NetClient<>();

        client.processor = processor;
        client.protocol = protocol;
        client.pool = new ResourcePool(Runtime.getRuntime().availableProcessors(), ()->{
            NioClientConnector<T> connector = new NioClientConnector<T>();
            connector.setProcessor(processor);
            connector.setProtocol(protocol);
            connector.setAddress(hostname,port);

            try {
                connector.connection();

                return connector;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        });

        return client;
    }

    /**
     * 发送
     */
    public void send(T message) throws IOException{
        NioClientConnector<T> c = pool.apply();
        c.send(message);
        pool.free();
    }
}
