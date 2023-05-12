package IO;

import java.io.File;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 18:03
 */
public class IODemo5 {
    public static void main(String[] args) {
        File file = new File("./test");
        File dest  = new File("./testAAA");
        file.renameTo(dest);
    }
}
