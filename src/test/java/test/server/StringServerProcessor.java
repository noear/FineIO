package test.server;

import org.noear.fineio.core.NetSession;
import org.noear.fineio.core.MessageProcessor;

public class StringServerProcessor implements MessageProcessor<String> {
    @Override
    public void process(NetSession session, String message) {

        try {
            System.out.println("收到：" + message);
            session.write("滚".getBytes());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
