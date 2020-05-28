package org.noear.fineio.core;

import org.noear.fineio.core.utils.RunnableEx;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

public class NetRunner {
    private static final String err_broken_pipe ="Broken pipe";
    private static final String err_protocol_wrong = "Protocol wrong type for socket";

    public static void run(RunnableEx<Throwable> runnable){
        if(runnable == null){
            return;
        }

        try{
            runnable.run();
        }catch (Throwable ex){}
    }

    public static void run(RunnableEx<Throwable> runnable, RunnableEx<Throwable> onClosed){
        try {
            runnable.run();
        } catch (ClosedChannelException ex) {
            run(onClosed);
        } catch (IOException ex) {
            if(err_broken_pipe.equals(ex.getMessage()) || err_protocol_wrong.equals(ex.getMessage())) {
                run(onClosed);
            }else{
                ex.printStackTrace();
            }
        } catch (Throwable ex){
            ex.printStackTrace();
        }
    }
}
