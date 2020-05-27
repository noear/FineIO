package org.noear.fineio.http;

import org.noear.fineio.core.MessageHandler;
import org.noear.fineio.core.NetSession;

public class HttpEntityHandler implements MessageHandler<HttpEntity> {
    @Override
    public void handle(NetSession<HttpEntity> session, HttpEntity message) throws Throwable {

    }
}
