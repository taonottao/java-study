package IO;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 21:46
 */
public class IODemo9 {
    public static void main(String[] args) {
        try (Writer writer = new FileWriter("e:/test.txt")){
            writer.write("hello world");
            //手动刷新缓冲区
            writer.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
