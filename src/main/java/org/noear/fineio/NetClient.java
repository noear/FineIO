package org.noear.fineio;

import org.noear.fineio.nio.NioClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 网络客户端
 * */
public abstract class NetClient<T> {
    /**
     * Nio net server
     */
    public static <T> NetClient<T> nio(Protocol<T> protocol, MessageProcessor<T> processor) {
        NetClient<T> client = new NioClient<T>();
        client.protocol = protocol;
        client.processor = processor;

        return client;
    }

    /**
     * 消息处理器
     */
    protected MessageProcessor<T> processor;
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
        new Thread(() -> {
            try {
                connection(hostname, port);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }).start();

        return this;
    }

    /**
     * 发送
     */
    public abstract void send(ByteBuffer buffer) throws IOException;

    /**
     * 发送
     */
    public void send(byte[] bytes)  throws IOException{
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        send(buffer);
    }

    /**
     * 关闭
     */
    public void colse() {
        colsed = true;
    }

    protected boolean colsed = false;
}
