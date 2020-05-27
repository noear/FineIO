package test.stringTest.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageHandler;
import org.noear.fineio.core.NetClient;
import test.stringTest.StringProtocol;
import test._future.CallUtil;

public class ClientTest1 {
    public static void main(String[] args) {
        //定义代理
        //
        MessageHandler<String> handler = (session, message)->{
            System.out.println("客户端：收到：" + message);
        };

        //定义客户端
        //
        var client = FineIO.client(new StringProtocol()).handle(handler).bind("localhost", 8080);

        //测试（请选启动服务端）
        //
        CallUtil.call(()->client.send("测试1"));
        CallUtil.call(()->client.send("测试2"));
    }
}
