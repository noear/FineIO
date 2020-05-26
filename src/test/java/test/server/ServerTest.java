package test.server;

import org.noear.fineio.FineIO;
import test.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        FineIO.server(new StringProtocol(), new StringServerProcessor())
                .bind("localhost", 8080)
                .start(false);
    }
}
