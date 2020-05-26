package test.server;

import org.noear.fineio.NetSession;
import org.noear.fineio.MessageProcessor;

public class StringServerProcessor implements MessageProcessor<String> {
    @Override
    public void process(NetSession<String> session) {
        String message = session.message();

        try {
            System.out.println("收到：" + message);
            session.writeAndFlush("滚".getBytes());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}