package ThreadStudy.newStudy;

import java.sql.SQLOutput;

/**
 * 模拟实现烧水泡茶
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/11/4 23:35
 */
public class Demo1 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "洗水壶");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "烧开水");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, "老王");

        Thread t2 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "洗茶壶");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "洗茶杯");
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + "拿茶叶");
                Thread.sleep(1000);
                t1.join();
                System.out.println(Thread.currentThread().getName() + "泡茶");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "小王");

        t1.start();
        t2.start();
    }
}
