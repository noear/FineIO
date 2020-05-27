package test;

import org.noear.fineio.core.Protocol;

import java.nio.ByteBuffer;

public class StringProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer buffer) {
        int size = buffer.getInt();
        if(size > 0) {
            byte[] bytes = new byte[size];
            buffer.get(bytes);

            return new String(bytes);
        }else{
            return null;
        }
    }

    @Override
    public ByteBuffer encode(String meaage) {
        byte[] bytes = meaage.getBytes();
        int size = bytes.length;

        ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length + 4);

        buf.putInt(size);
        buf.put(bytes);
        buf.flip();

        return buf;
    }
}
