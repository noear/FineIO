package test._future;

public class CallUtil {
    public static void call(Act0Ex fun){
        try{
            fun.run();
        }catch (Throwable ex){
            ex.printStackTrace();
        }
    }
}
