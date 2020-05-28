package org.noear.fineio.tcp;

import org.noear.fineio.core.IoConfig;
import org.noear.fineio.core.NetServer;
import org.noear.fineio.core.Protocol;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;

public class NioTcpServer<T> extends NetServer<T> {
    private Selector selector;
    private NioTcpAcceptor<T> acceptor;

    public NioTcpServer(Protocol<T> protocol) {
        this(protocol, new IoConfig<T>());
    }

    public NioTcpServer(Protocol<T> protocol, IoConfig<T> cfg) {
        super(cfg);
        config.setProtocol(protocol);
        acceptor = new NioTcpAcceptor<>(config, true);
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
                    }
                    catch (Throwable ex) {
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
            acceptor.accept(key,selector);
        }

        if (key.isReadable()) {
            acceptor.read(key);
        }
    }
}
