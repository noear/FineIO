package test;

import org.noear.fineio.Protocol;

import java.nio.ByteBuffer;

public class StringProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return new String(bytes);
    }
}
