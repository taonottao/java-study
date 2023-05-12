package IO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 18:25
 */
public class IODemo6 {
    public static void main(String[] args) throws IOException {
        //使用字节流来读取文件
        //创建InputStream 对象的时候, 使用绝对路径和相对路径都是可以的. 也可以使用 File对象
        InputStream inputStream = new FileInputStream("e:/test.txt");

        // 进行读操作
//        while(true){
//            int b = inputStream.read();
//            if(b == -1){
//                // 读取完毕
//                break;
//            }
//            System.out.printf("%x\n", (byte)b);
//        }

        while(true){
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            System.out.println("len: " + len);
            if(len == -1){
                break;
            }
            // 此时读取的结果就被放到了byte数组中
            for (int i = 0; i < len; i++) {
                System.out.printf("%x\n", buffer[i]);
            }
        }

        inputStream.close();
    }
}
