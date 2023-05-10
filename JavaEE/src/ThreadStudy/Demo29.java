package ThreadStudy;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/9 9:19
 */

class MyThreadPool{
    //此处不涉及到"时间", 此处只有任务, 就直接使用 Runnable 即可
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    //n 表示线程数量
    public MyThreadPool(int n){
        //在这里创建出线程
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() ->{
                while(true){
                    try {
                        Runnable runnable = queue.take();
                        runnable.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    //注册任务给线程池
    public void submit(Runnable runnable){
        try {
            queue.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Demo29 {
    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(10);
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
