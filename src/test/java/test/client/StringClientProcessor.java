package test.client;

import org.noear.fineio.SessionProcessor;
import org.noear.fineio.NetSession;

public class StringClientProcessor implements SessionProcessor<String> {
    int count = 0;
    int total = 1000 * 1000;
    long time_start = 0;

    @Override
    public void process(NetSession<String> session) {
        String message = session.message();

        try {
            System.out.println(Thread.currentThread().getName()+"-客户端：收到：" + message);

//            if (count == 0) {
//                time_start = System.currentTimeMillis();
//            }
//
//            if (count < total) {
//                session.writeAndFlush(("测试消息" + count).getBytes());
//                count++;
//            }
//
//            if (count == total) {
//                System.out.println(total+"-客户端用时：" + (System.currentTimeMillis() - time_start));
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
