package org.noear.fineio.core;

import java.nio.ByteBuffer;

/**
 * 编码协议
 * */
public interface IoProtocol<T> {
    T decode(final ByteBuffer buffer);
    ByteBuffer encode(T meaage);
}
