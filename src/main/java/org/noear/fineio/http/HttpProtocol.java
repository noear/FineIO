package org.noear.fineio.http;

import org.noear.fineio.core.Protocol;

import java.nio.ByteBuffer;

public class HttpProtocol implements Protocol<HttpEntity> {
    @Override
    public HttpEntity decode(ByteBuffer buffer) {
        return null;
    }

    @Override
    public ByteBuffer encode(HttpEntity meaage) {
        return null;
    }
}
