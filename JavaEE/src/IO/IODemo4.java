package IO;

import java.io.File;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/12 17:52
 */
public class IODemo4 {
    public static void main(String[] args) {
        File dir = new File("./test/aaa/bbb");
        //只能创建一级目录
//        dir.mkdir();
        //可以创建多级目录
        dir.mkdirs();
    }
}
