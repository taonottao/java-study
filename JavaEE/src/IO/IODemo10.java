package IO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 21:54
 */
public class IODemo10 {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
        try(InputStream inputStream = new FileInputStream("e:/test.txt")){
            Scanner scanner = new Scanner(inputStream);

            // 此时 读取的内容就是从文件进行读取了
            scanner.next();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
