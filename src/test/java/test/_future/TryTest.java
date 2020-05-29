package test._future;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TryTest {
    @Test
    public void test1() throws IOException {

        try(InputStream bb = null){
            if(bb == null){
                System.out.println("是个 null");
            }else{
                System.out.println("ok");
            }
        }
    }

    private InputStream get(){
        return new ByteArrayInputStream("".getBytes());
    }
}
