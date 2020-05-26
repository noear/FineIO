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

    public NetClient<T> bind(InetSocketAddress address) {
        this.address = address;

        this.pool = new ResourcePool<NioClientConnector<T>>(Runtime.getRuntime().availableProcessors(), ()->{
            NioClientConnector<T> connector = new NioClientConnector<T>();
            connector.setProcessor(processor);
            connector.setProtocol(protocol);
            connector.setAddress(address);

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

        return this;
    }

    public NetClient<T> bind(String hostname, int port) {
        if (hostname == null) {
            return bind(new InetSocketAddress(port));
        } else {
            return bind(new InetSocketAddress(hostname, port));
        }
    }

    /**
     * Nio net server
     */
    public static <T> NetClient<T> nio(Protocol<T> protocol, SessionProcessor<T> processor) {
        NetClient<T> client = new NetClient<>();
        client.protocol = protocol;
        client.processor = processor;

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
