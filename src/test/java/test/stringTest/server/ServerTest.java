package test.stringTest.server;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageHandler;
import test.stringTest.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        //定义代理
        //
        MessageHandler<String> handler = (session, message)->{
                System.out.println("我收到：" + message);
                Thread.sleep(10);
                session.write("我收到：" + message);
        };

        //启动服务
        //
        FineIO.server(new StringProtocol())
                .bind("localhost", 8080)
                .handle(handler.pools()) //handler.pools() //线程池模式
                .start(false);
    }
}
