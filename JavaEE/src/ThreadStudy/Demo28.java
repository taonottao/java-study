package ThreadStudy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用一下标准库的线程池
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/8 12:02
 */

public class Demo28 {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            int n = i;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("hello " + n);
                }
            });
        }
    }
}
