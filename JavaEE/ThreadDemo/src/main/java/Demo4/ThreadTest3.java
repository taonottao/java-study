package Demo4;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/23 11:37
 */
public class ThreadTest3 {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
//        BlockingQueue<String> queue = new ArrayBlockingQueue<>();
        queue.put("hello1");
        queue.put("hello2");
        queue.put("hello3");
        queue.put("hello4");
        queue.put("hello5");

        String ret = null;
        ret = queue.take();
        System.out.println(ret);
        ret = queue.take();
        System.out.println(ret);
        ret = queue.take();
        System.out.println(ret);
        ret = queue.take();
        System.out.println(ret);
        ret = queue.take();
        System.out.println(ret);
        ret = queue.take();
        System.out.println(ret);
    }
}
