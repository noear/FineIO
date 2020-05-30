package test.stringTest;

import org.noear.fineio.core.Protocol;

import java.nio.ByteBuffer;

public class StringProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer buffer) {
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt();
        if (length > buffer.remaining()) {
            buffer.reset();
            return null;
        }
        byte[] b = new byte[length];
        buffer.get(b);
        buffer.mark();
        return new String(b);
    }

    @Override
    public byte[] encode(String message) {
        return message.getBytes();
    }
}
