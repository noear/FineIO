package org.noear.fineio;

import org.noear.fineio.nio.NioClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 网络客户端
 * */
public abstract class NetClient<T> {
    static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * Nio net server
     */
    public static <T> NetClient<T> nio(Protocol<T> protocol, SessionProcessor<T> processor) {
        NetClient<T> client = new NioClient<T>();
        client.protocol = protocol;
        client.processor = processor;

        return client;
    }

    /**
     * 消息处理器
     */
    protected SessionProcessor<T> processor;
    protected Protocol<T> protocol;

    /**
     * 连接
     */
    public abstract void connection(InetSocketAddress address) throws IOException;

    /**
     * 连接
     */
    public void connection(String hostname, int port) throws IOException {
        if (hostname == null) {
            connection(new InetSocketAddress(port));
        } else {
            connection(new InetSocketAddress(hostname, port));
        }
    }

    /**
     * 连接（在一个新的线程）
     */
    public NetClient<T> connectionOnThread(String hostname, int port) {
        pool.execute(()->{
            try {
                connection(hostname, port);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        });

        return this;
    }

    /**
     * 发送
     */
    public abstract void send(T message) throws IOException;
    /**
     * 发送并关闭
     */
    public  void sendAndColse(T message) throws IOException{
        send(message);
        colse();
    }

    /**
     * 关闭
     */
    public void colse() {
        colsed = true;
    }

    protected boolean colsed = false;
}
