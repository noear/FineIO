package test.stringTest.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageHandler;
import org.noear.fineio.core.NetClient;
import test.stringTest.StringProtocol;

public class ClientTest1 {
    public static void main(String[] args) {
        //定义代理
        //
        MessageHandler<String> handler = (session, message)->{
            System.out.println("客户端：收到：" + message);
        };

        //定义客户端
        //
        NetClient<String> client = FineIO.client(new StringProtocol()).handle(handler).bind("localhost", 8888);

        //测试（请选启动服务端）
        //
        client.send("测试1");
        client.send("测试2");
    }
}
