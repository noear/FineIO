package test.stringTest.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageHandler;
import org.noear.fineio.core.NetClient;
import test.stringTest.StringProtocol;
import test._future.CallUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest2 {
    public static void main(String[] args) {
        //定义代理
        //
        int taskTotal = 1000;
        long time_start = System.currentTimeMillis();

        MessageHandler<String> handler = (session, message) -> {
            System.out.println(Thread.currentThread().getName() + "-客户端-收到：" + message + " -- " + (System.currentTimeMillis() - time_start));
        };

        //定义客户端
        //
        NetClient<String> client = FineIO.client(new StringProtocol()).handle(handler).bind("localhost", 8888);

        //测试（请选启动服务端）
        //
        ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        for (int i = 0; i < taskTotal; i++) {
            Integer no = i;
            executors.execute(() -> {
                CallUtil.call(() -> client.send("测试" + no));
            });
        }
    }
}
