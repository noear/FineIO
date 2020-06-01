package test.stringTest.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.IoConfig;
import org.noear.fineio.core.MessageHandler;
import org.noear.fineio.core.NetClient;
import org.noear.fineio.core.Sender;
import test.stringTest.StringProtocol;

import java.io.IOException;

public class ClientTest3 {
    public static void main(String[] args) {
        long time_start = System.currentTimeMillis();

        MessageHandler<String> handler = (session, message) -> {
            //System.out.println(Thread.currentThread().getName() + "-客户端-收到：" + message + " -- " + (System.currentTimeMillis() - time_start));
        };

        //定义客户端
        //
        IoConfig<String> cfg  =new IoConfig<>();
        cfg.setWriteBufferSize(1024 * 3);
        NetClient<String> client = FineIO.client(new StringProtocol(), cfg).handle(handler).bind("localhost", 8888);

        //测试（请选启动服务端）
        //
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        test(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
    }

    private static void test(NetClient<String> client) throws IOException{
        StringBuffer sb = new StringBuffer(1024);
        Sender<String> sender = client.getConnector();

        while (true) {
            sb.setLength(0);
            int num = (int) (Math.random() * 10) + 1;

            while (num-- > 0) {
                sb.append("Hello-FineIO");
            }

            sender.send(sb.toString());
        }
    }
}
