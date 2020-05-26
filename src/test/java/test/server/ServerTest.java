package test.server;

import org.noear.fineio.NetServer;
import test.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        NetServer.nio(new StringProtocol(), new StringServerProcessor())
                .startOnThread("localhost", 8080);
    }
}
