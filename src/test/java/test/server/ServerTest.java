package test.server;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageProcessor;
import org.noear.fineio.core.MessageProcessorPool;
import test.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        //定义处理器
        //
        MessageProcessor<String> processor = (session,message)->{
                System.out.println("收到：" + message);
                //Thread.sleep(10);
                session.write("别来防我");
        };

        //启动服务
        //
        FineIO.server(new StringProtocol())
                .process(new MessageProcessorPool<>(processor))
                .bind("localhost", 8080)
                .start(false);
    }
}
