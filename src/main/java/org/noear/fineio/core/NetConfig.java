package org.noear.fineio.core;

import java.net.InetSocketAddress;

public class NetConfig<T> {
    /**
     * 处理器
     * */
    private MessageProcessor<T> processor;
    /**
     * 协议
     * */
    private Protocol<T> protocol;
    /**
     * 地址
     * */
    private InetSocketAddress address;

    public MessageProcessor<T> getProcessor() {
        return processor;
    }

    public Protocol<T> getProtocol() {
        return protocol;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setProcessor(MessageProcessor<T> processor) {
        this.processor = processor;
    }
    public void setProtocol(Protocol<T> protocol) {
        this.protocol = protocol;
    }
    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }
    public void setAddress(String hostname, int port) {
        if (hostname == null) {
            setAddress(new InetSocketAddress(port));
        } else {
            setAddress(new InetSocketAddress(hostname, port));
        }
    }
}
