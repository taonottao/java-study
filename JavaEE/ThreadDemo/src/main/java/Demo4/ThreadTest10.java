package Demo4;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/8/14 19:17
 */
public class ThreadTest10 {

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger m = new AtomicInteger(0);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                // 这个就相当于 前置++
                m.getAndIncrement();
            }
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                m.getAndIncrement();
            }
        });
        t2.start();

        t1.join();
        t2.join();
        System.out.println(m);
    }

}
