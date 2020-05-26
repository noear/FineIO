package org.noear.fineio;

import java.nio.ByteBuffer;

public interface Protocol<T> {
    T request(final ByteBuffer buffer);
    ByteBuffer encode(T meaage);
}
