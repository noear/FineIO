package org.noear.fineio;

import org.noear.fineio.extension.ResourcePool;
import org.noear.fineio.nio.NioClientConnector;

import java.io.IOException;

/**
 * 网络客户端
 * */
public class NetClient<T> {

    private ResourcePool<NioClientConnector<T>> pool;

    /**
     * Nio net server
     */
    public static <T> NetClient<T> nio(Protocol<T> protocol, SessionProcessor<T> processor, String hostname, int port) {
        NetClient<T> client = new NetClient<>();

        client.pool = new ResourcePool<NioClientConnector<T>>(Runtime.getRuntime().availableProcessors(), ()->{
            NioClientConnector<T> connector = new NioClientConnector<T>();
            connector.setProcessor(processor);
            connector.setProtocol(protocol);
            connector.setAddress(hostname, port);

            try {
                connector.connection();

                return connector;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }){
            @Override
            protected NioClientConnector<T> open(NioClientConnector<T> res) {
                if(res.isOpen()){
                    return res;
                }else {
                    return null;
                }
            }
        };

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
