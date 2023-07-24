package Demo4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于生产者消费者模型写一个阻塞队列
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/23 18:26
 */
public class ThreadTest4 {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        // 生产者
        Thread t1 = new Thread(() -> {
            int value = 0;
            while (true) {
                try {
                    System.out.println("生产元素：" + value);
                    queue.put(value);
                    value++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();

        // 消费者
        Thread t2 = new Thread(() -> {
            while (true) {
                int value = 0;
                try {
                    value = queue.take();
                    System.out.println("消费元素：" + value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t2.start();
    }
}
