package IO;

import java.io.File;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 17:43
 */
public class IODemo3 {
    public static void main(String[] args) {
        File file = new File("./test.txt");
        file.delete();
    }
}
