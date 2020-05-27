package org.noear.fineio.solution.nio;

import org.noear.fineio.core.NetConfig;
import org.noear.fineio.core.NetServer;
import org.noear.fineio.core.Protocol;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioTcpServer<T> extends NetServer<T> {
    private Selector selector;
    private NioTcpAcceptor<T> acceptor;

    public NioTcpServer(Protocol<T> protocol) {
        this(protocol, new NetConfig<T>());
    }

    public NioTcpServer(Protocol<T> protocol, NetConfig<T> cfg) {
        super(cfg);
        config.setProtocol(protocol);
        acceptor = new NioTcpAcceptor<>(config);
    }

    /**
     * 开始
     */
    @Override
    public void start(boolean blocking) {
        if (blocking) {
            try {
                start0();
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        } else {
            new Thread(() -> {
                start(true);
            }).start();
        }
    }

    private void start0() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(config.getAddress());

        selector = Selector.open();

        ssc.register(selector, SelectionKey.OP_ACCEPT);

        startDo();
    }

    private void startDo() {
        while (!stopped) {
            try {
                if (selector.select(1000) < 1) {
                    continue;
                }

                Iterator<SelectionKey> keyS = selector.selectedKeys().iterator();

                while (keyS.hasNext()) {
                    SelectionKey key = keyS.next();
                    keyS.remove();

                    try {
                        selectDo(key);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        if (selector != null) {
            try {
                selector.close();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private void selectDo(SelectionKey key) throws IOException {
        if (key == null || key.isValid() == false) {
            return;
        }

        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            if (channel == null) {
                return;
            }



            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, Boolean.TRUE);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            return;
        }

        if (key.isReadable()) {
            acceptor.receive(key);
        }
    }
}
