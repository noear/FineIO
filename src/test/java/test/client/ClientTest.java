package test.client;

import org.noear.fineio.NetClient;
import test.StringProtocol;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            list.add(i);
        }

        list.stream().forEach(i -> {
            try {
                NetClient.nio(new StringProtocol(), new StringClientProcessor())
                        .connectionOnThread("localhost", 8080)
                        .sendAndColse("测试" + i);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });


    }
}
