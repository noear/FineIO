package org.noear.fine;

import java.nio.ByteBuffer;

public interface NetProtocol<T> {
    T decode(final ByteBuffer buffer);
}
