package Demo4;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 手动模拟实现线程池
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/25 10:48
 */

class MyThreadPool {
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void submit(Runnable runnable) throws InterruptedException {
        queue.put(runnable);
    }

    public MyThreadPool(int n) {
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                try {
                    while (true){
                        Runnable runnable = queue.take();
                        runnable.run();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            t.start();
        }
    }
}

public class ThreadTest9 {

    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            int num = i;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("hello " + num);
                }
            });
        }
    }

}
