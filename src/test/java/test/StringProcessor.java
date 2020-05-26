package test;

import org.noear.fineio.NetSession;
import org.noear.fineio.SessionProcessor;

public class StringProcessor implements SessionProcessor<String> {
    @Override
    public void process(NetSession<String> session) {
        String message = session.message();

        session.writeAndFlush(("收到：" + message).getBytes());
    }
}
