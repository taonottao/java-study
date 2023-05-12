package IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 21:08
 */
public class IODemo7 {
    public static void main(String[] args) throws IOException {
        //方法一:
//        OutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream("e:/test.txt");
//
//            outputStream.write(97);
//            outputStream.write(98);
//            outputStream.write(99);
//            outputStream.write(100);
//        } finally {
//            outputStream.close();
//        }
//
        //方法二: 更推荐
        try(OutputStream outputStream = new FileOutputStream("e:/test.txt")){
            outputStream.write(97);
            outputStream.write(98);
            outputStream.write(99);
            outputStream.write(100);
        }

    }
}
