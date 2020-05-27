package test._future;

public class CallUtil {
    public static void call(Act0Ex fun){
        try{
            fun.run();
        }catch (Throwable ex){
            System.out.println(Thread.currentThread().getName());
            ex.printStackTrace();
        }
    }
}
