package test;

import org.noear.fineio.Protocol;

import java.nio.ByteBuffer;

public class StringProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer buffer) {
        return new String(buffer.array());
    }
}
