package test.httpTest;

import org.noear.fineio.FineIO;
import org.noear.fineio.http.HttpEntityHandler;
import org.noear.fineio.http.HttpProtocol;

public class ServerTest {
    public static void main(String[] args) {
        FineIO.server(new HttpProtocol())
                .handle(new HttpEntityHandler())
                .bind(null, 8888)
                .start(false);
    }
}
