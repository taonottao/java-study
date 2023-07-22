package Demo2;

import java.util.concurrent.BrokenBarrierException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/21 10:16
 */
public class ThreadTest4 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            // currentThread() 是获取到当前线程(t 线程)
            // isInterrupted() 就相当于 t 线程的自带的一个标志位
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("t, 启动！");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        t.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 把 t 内部的标志位设置成 true
        t.interrupt();
    }
}
