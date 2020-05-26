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
     * */
    public static <T> NetClient<T> nio(Protocol<T> protocol, SessionProcessor<T> processor) {
        NetClient<T> server = new NioClient<T>();
        server.protocol = protocol;
        server.processor = processor;

        return server;
    }

    /**
     * 消息处理器
     * */
    protected SessionProcessor<T> processor;
    protected Protocol<T> protocol;

    /**
     * 连接
     * */
    public abstract void connection(InetSocketAddress address) throws IOException;

    /**
     * 发送
     * */
    public abstract void send(ByteBuffer buffer) throws Exception;

    /**
     * 关闭
     * */
    public void colse() {
        colsed = true;
    }

    protected boolean colsed = false;
}
