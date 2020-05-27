package test.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.NetClient;
import test.StringProtocol;

public class ClientTest2 {
    public static void main(String[] args) {
        NetClient<String> client = FineIO.client(new StringProtocol())
                .receive(new StringClientProcessor())
                .bind("localhost", 8080);

        try {
            client.send("测试1");
            client.send("测试2");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
