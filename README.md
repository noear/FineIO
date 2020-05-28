# FineIO
基于NIO实现的轻量级通讯框架

## 特性

暂无...

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

        //定义客户端（内置连接池功能）
        //
        var client = FineIO.client(new StringProtocol()).handle(handler).bind("localhost", 8080);

        //测试（请先启动服务端）
        //
        client.send("测试1");
        client.send("测试2");
    }
}
```

### 编码协议
```java
public class StringProtocol implements Protocol<String> {
    @Override
    public String decode(ByteBuffer buffer) {
        if (buffer.remaining() > Integer.BYTES) {
            int size = buffer.getInt();

            if (size > 0 && size <= buffer.remaining()) {
                byte[] bytes = new byte[size];
                buffer.get(bytes);

                return new String(bytes);
            }
        }

        return null;
    }

    @Override
    public ByteBuffer encode(String message) {
        byte[] bytes = message.getBytes();

        ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length + Integer.BYTES);

        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();

        return buf;
    }
}
```

## 性能报告

暂无...