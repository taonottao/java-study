package ThreadStudy;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/4 20:51
 */
public class Demo21 {
    public static void main(String[] args) throws InterruptedException {
        BlockingDeque<String> queue = new LinkedBlockingDeque<>();
        queue.put("hello");
        String s = queue.take();
    }
}
