# FineIO
基于NIO实现的轻量级通讯框架

## 特性

## 示例


### 服务端
```java
public class ServerTest {
    public static void main(String[] args) {
        //定义代理
        //
        MessageHandler<String> handler = (session, message)->{
                System.out.println("我收到：" + message);
                Thread.sleep(10);
                session.write("我收到：" + message);
        };

        //启动服务
        //
        FineIO.server(new StringProtocol())
                .bind("localhost", 8080)
                .handle(handler.pools()) //handler.pools() //线程池模式
                .start(false);
    }
}
```

### 客户端
```java
public class ClientTest1 {
    public static void main(String[] args) {
        //定义代理
        //
        MessageHandler<String> handler = (session, message)->{
            System.out.println("客户端：收到：" + message);
        };

        //定义客户端
        //
        NetClient<String> client = FineIO.client(new StringProtocol())
                .handle(handler)
                .bind("localhost", 8080);

        //测试（请选启动服务端）
        //
        CallUtil.call(()->client.send("测试1"));
        CallUtil.call(()->client.send("测试2"));
    }
}
```
