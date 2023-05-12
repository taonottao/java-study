package IO;

import java.io.File;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 17:39
 */
public class IODemo2 {
    public static void main(String[] args) throws IOException {
        File file = new File("./test.txt");

        file.createNewFile();

        System.out.println(file.exists());
        System.out.println(file.isFile());
        System.out.println(file.isDirectory());
    }
}
