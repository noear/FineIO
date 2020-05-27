package test.stringTest.server;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageProcessor;
import test.stringTest.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        //定义处理器
        //
        MessageProcessor<String> processor = (session,message)->{
                System.out.println("收到：" + message);
                Thread.sleep(10);
                session.write("收到：" + message);
        };

        //启动服务
        //
        FineIO.server(new StringProtocol())
                .process(processor.pools())
                .bind("localhost", 8080)
                .start(false);



        /**
         * 处理时间短的：processor
         * 处理时间长的：10ms或以上，用 processor.pools() //线程池模式
         * */
    }
}
