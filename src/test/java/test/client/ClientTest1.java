package test.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageProcessor;
import org.noear.fineio.core.NetClient;
import org.noear.fineio.core.NetClientConnector;
import test.StringProtocol;
import test._future.CallUtil;

public class ClientTest1 {
    public static void main(String[] args) {
        //定义接收处理器
        //
        MessageProcessor<String> processor = (session,message)->{
            System.out.println(Thread.currentThread().getName()+"-客户端：收到：" + message);
        };

        //定义客户端
        //
        NetClient<String> client = FineIO.client(new StringProtocol())
                .receive(processor)
                .bind("localhost", 8080);

        NetClientConnector<String> connector = client.getConnector();

        //测试（请选启动服务端）
        //
        CallUtil.call(()->connector.send("测试1"));
        CallUtil.call(()->connector.send("测试2"));
    }
}
