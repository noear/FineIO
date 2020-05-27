package test.server;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageProcessor;
import org.noear.fineio.core.MessageProcessorPool;
import test.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {

        long time_start = System.currentTimeMillis();

        //定义处理器
        //
        MessageProcessor<String> processor = (session,message)->{
            try {
                System.out.println("收到：" + message);
                //Thread.sleep(10);
                session.write("别来防我");
            }catch (Throwable ex){
                ex.printStackTrace();
            }
        };

        //启动服务
        //
        FineIO.server(new StringProtocol())
                .process(new MessageProcessorPool<>(processor))
                .bind("localhost", 8080)
                .start(false);
    }
}
