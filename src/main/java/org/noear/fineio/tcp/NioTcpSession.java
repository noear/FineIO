package org.noear.fineio.tcp;

import org.noear.fineio.core.IoConfig;
import org.noear.fineio.core.NetSession;
import org.noear.fineio.core.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioTcpSession<T> extends NetSession<T> {
    private SocketChannel channel;
    private IoConfig<T> config;
    private final ByteBuffer writeBuffer;
    private final int writeBufferLimit;

    public NioTcpSession(SocketChannel channel, IoConfig<T> config){
        this.channel = channel;
        this.config = config;

        this.writeBuffer = ByteBuffer.allocateDirect(config.getBufferSize());
        this.writeBufferLimit = config.getBufferSize() / 2;
    }

    /**
     * 写缓存
     * */
    @Override
    public void write(T message) throws IOException {
        if(message == null){
            return;
        }

        if(isValid()) {
            synchronized (writeBuffer) {
                byte[] bytes = config.getProtocol().encode(message);
                if (bytes.length >= writeBufferLimit) {
                    writeBuffer.putInt(bytes.length);
                    push0();
                    channel.write(ByteBuffer.wrap(bytes));
                } else {
                    writeBuffer.putInt(bytes.length);
                    writeBuffer.put(bytes);

                    if (writeBuffer.position() >= writeBufferLimit) {
                        push0();
                    }
                }
            }
        }
    }

    private void push0() throws IOException{
        writeBuffer.flip();
        channel.write(writeBuffer);
        writeBuffer.position(0);
        writeBuffer.limit(writeBuffer.capacity());
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException{
        return (InetSocketAddress)channel.getLocalAddress();
    }

    @Override
    public  InetSocketAddress getRemoteAddress() throws IOException{
        return (InetSocketAddress)channel.getRemoteAddress();
    }

    @Override
    public boolean isValid() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

}
