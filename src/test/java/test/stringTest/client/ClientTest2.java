package test.stringTest.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageHandler;
import org.noear.fineio.core.NetClient;
import test.stringTest.StringProtocol;
import test._future.CallUtil;

import java.util.ArrayList;
import java.util.List;

public class ClientTest2 {
    public static void main(String[] args) {
        //定义接收处理器
        //
        int taskTotal = 1000;
        long time_start = System.currentTimeMillis();

        MessageHandler<String> processor = (session, message) -> {
            System.out.println(Thread.currentThread().getName() + "-客户端-收到：" + message + " -- " +  (System.currentTimeMillis() - time_start));
        };

        //定义客户端
        //
        NetClient<String> client = FineIO.client(new StringProtocol())
                .handle(processor)
                .bind("localhost", 8080);

        //测试（请选启动服务端）
        //
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < taskTotal; i++) {
            list.add(i);
        }

        list.parallelStream().forEach(i -> {
            CallUtil.call(() -> client.send("测试" + i));
        });
    }
}