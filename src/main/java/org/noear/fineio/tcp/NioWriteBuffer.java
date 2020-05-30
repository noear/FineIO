package org.noear.fineio.tcp;

import org.noear.fineio.core.IoConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioWriteBuffer<T> {
    private final ByteBuffer writeBuffer;
    private final int writeBufferLimit;
    private IoConfig<T> config;
    private SocketChannel channel;

    public NioWriteBuffer(IoConfig<T> config, SocketChannel channel) {
        this.config = config;
        this.channel = channel;

        writeBuffer = ByteBuffer.allocateDirect(config.getBufferSize());
        writeBufferLimit = config.getBufferSize() / 2;
    }

    public void write(T message) throws IOException{
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

    private void push0() throws IOException {
        writeBuffer.flip();
        channel.write(writeBuffer);
        writeBuffer.position(0);
        writeBuffer.limit(writeBuffer.capacity());
    }
}
