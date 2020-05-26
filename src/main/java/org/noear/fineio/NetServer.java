package org.noear.fineio;

import org.noear.fineio.nio.NioServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 网络服务器
 * */
public abstract class NetServer<T> {
    /**
     * Nio net server
     * */
    public static <T> NetServer<T> nio(Protocol<T> protocol, SessionProcessor<T> processor) {
        NetServer<T> server = new NioServer<T>();
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
     * 启动
     * */
    public abstract void start(InetSocketAddress address) throws IOException;
    public void start(String hostname, int port) throws IOException {
        if (hostname == null) {
            start(new InetSocketAddress(port));
        } else {
            start(new InetSocketAddress(hostname, port));
        }
    }

    /**
     * 启动（在一个新的线程）
     * */
    public void startOnThread(String hostname, int port){
        new Thread(() -> {
            try {
                start(hostname, port);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

    /**
     * 停止
     */
    public void stop() {
        stopped = true;
    }
    protected boolean stopped = false;
}
