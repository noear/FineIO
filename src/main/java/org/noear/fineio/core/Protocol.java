package org.noear.fineio.core;

import java.nio.ByteBuffer;

/**
 * 编码协议
 * */
public interface Protocol<T> {
    T decode(final ByteBuffer buffer);
    byte[] encode(T meaage);
}
