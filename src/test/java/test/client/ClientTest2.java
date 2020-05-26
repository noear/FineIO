package test.client;

import org.noear.fineio.FineNIO;
import org.noear.fineio.NetClient;
import test.StringProtocol;

import java.util.ArrayList;
import java.util.List;

public class ClientTest2 {
    public static void main(String[] args) {
        NetClient<String> client = FineNIO.client(new StringProtocol(), new StringClientProcessor())
                                         .bind("localhost", 8080);

        try {
            client.send("测试1");
            client.send("测试2");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
