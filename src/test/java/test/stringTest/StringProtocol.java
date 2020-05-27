package test.stringTest;

import org.noear.fineio.core.IoProtocol;

import java.nio.ByteBuffer;

public class StringProtocol implements IoProtocol<String> {
    @Override
    public String decode(ByteBuffer buffer) {
        if (buffer.remaining() > 4) {
            int size = buffer.getInt();

            if (size > 0 && size <= buffer.remaining()) {
                byte[] bytes = new byte[size];
                buffer.get(bytes);

                return new String(bytes);
            }
        }

        return null;
    }

    @Override
    public ByteBuffer encode(String meaage) {
        byte[] bytes = meaage.getBytes();

        ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length + 4);

        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();

        return buf;
    }
}
