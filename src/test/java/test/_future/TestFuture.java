package test._future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestFuture {

    public static void main(String[] args) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            try {
                String str = completableFuture.get(3, TimeUnit.SECONDS);

                System.out.println(str);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            completableFuture.complete("test");
        }).start();

    }
}
