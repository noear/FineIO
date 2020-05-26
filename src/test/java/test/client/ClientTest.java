package test.client;

import org.noear.fineio.NetClient;
import test.StringProtocol;

import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    public static void main(String[] args) {




        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        list.parallelStream().forEach(i -> {
            try {
                NetClient.nio(new StringProtocol(), new StringClientProcessor())
                        .connectionOnThread("localhost", 8080)
                        .send(("测试" + i).getBytes());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });


    }
}
