package org.noear.fineio.core;

import java.nio.ByteBuffer;

/**
 * 编码协议
 * */
public interface Protocol<T> {
    T request(final ByteBuffer buffer);
    ByteBuffer encode(T meaage);
}
