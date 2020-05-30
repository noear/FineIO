package org.noear.fineio.tcp;

import org.noear.fineio.core.IoConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioWriteBuffer<T> {
    private final ByteBuffer buffer;
    private final int bufferLimit;
    private IoConfig<T> config;
    private SocketChannel channel;

    public NioWriteBuffer(IoConfig<T> config, SocketChannel channel) {
        this.config = config;
        this.channel = channel;

        buffer = ByteBuffer.allocateDirect(config.getWriteBufferSize());
        bufferLimit = config.getWriteBufferSize() / 2;
    }

    public void write(T message) throws IOException{
        synchronized (buffer) {
            byte[] bytes = config.getProtocol().encode(message);
            if (bytes.length >= bufferLimit) {
                buffer.putInt(bytes.length);
                push0();

                channel.write(ByteBuffer.wrap(bytes));
            } else {
                buffer.putInt(bytes.length);
                buffer.put(bytes);

                if (buffer.position() >= bufferLimit) {
                    push0();
                }
            }
        }
    }

    private void push0() throws IOException {
        buffer.flip();
        channel.write(buffer);

        buffer.position(0);
        buffer.limit(buffer.capacity());
    }
}
