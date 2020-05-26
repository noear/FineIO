package test.client;

import org.noear.fineio.FineIO;
import org.noear.fineio.NetClient;
import test.StringProtocol;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {
        NetClient<String> client = FineIO.client(new StringProtocol(), new StringClientProcessor())
                                         .bind("localhost", 8080);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000 * 1000; i++) {
            list.add(i);
        }

        list.parallelStream().forEach(i -> {
            try {
                client.send("测试" + i);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }
}
