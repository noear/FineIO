package test.server;

import org.noear.fineio.FineIO;
import org.noear.fineio.core.MessageProcessorPool;
import test.StringProtocol;

public class ServerTest {
    public static void main(String[] args) {
        FineIO.server(new StringProtocol())
                .process(new MessageProcessorPool<>(new StringServerProcessor()))
                .bind("localhost", 8080)
                .start(false);
    }
}
