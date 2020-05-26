package test.server;

import org.noear.fineio.FineNIO;
import test.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        FineNIO.server(new StringProtocol(), new StringServerProcessor())
                .bind("localhost", 8080)
                .start(false);
    }
}
