package IO;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 21:40
 */
public class IODemo8 {
    // 字符流的操作
    public static void main(String[] args) {
        try(Reader reader = new FileReader("e:/test.txt")){
            while(true){
                int ch = reader.read();
                if(ch == -1){
                    break;
                }
                System.out.println("" + (char)ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
