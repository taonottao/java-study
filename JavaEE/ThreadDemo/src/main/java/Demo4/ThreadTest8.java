package Demo4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/24 18:17
 */
public class ThreadTest8 {
    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        pool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程池，启动！");
            }
        });
    }
}
