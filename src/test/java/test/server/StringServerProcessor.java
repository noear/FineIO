package test.server;

import org.noear.fineio.NetSession;
import org.noear.fineio.SessionProcessor;

public class StringServerProcessor implements SessionProcessor<String> {
    @Override
    public void process(NetSession<String> session) {
        String message = session.message();

        try {
            System.out.println("收到：" + message);
            session.write("滚".getBytes());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
