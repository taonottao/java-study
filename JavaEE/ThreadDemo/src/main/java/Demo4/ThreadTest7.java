package Demo4;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/24 12:13
 */

// 表示一个任务
class MyTask implements Comparable<MyTask> {
    public Runnable runnable;
    // 为了后续判定方便，我们使用绝对的时间戳
    public long time;

    public MyTask(Runnable runnable, long time) {
        this.runnable = runnable;
        // 取当前时间戳 + time，作为任务实际执行的时间戳
        this.time = System.currentTimeMillis() + time;
    }

    @Override
    public int compareTo(MyTask o) {
        return (int) (this.time - o.time);
    }
}

class MyTimer {
    PriorityBlockingQueue<MyTask> queue = new PriorityBlockingQueue<>();

    private Object locker = new Object();

    public void schedule(Runnable runnable, long time) {
        MyTask task = new MyTask(runnable, time);
        queue.put(task);
        synchronized (locker) {
            locker.notify();
        }
    }

    // 创建线程，负责执行任务
    public MyTimer() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    synchronized (locker) {
                        MyTask task = queue.take();
                        long curTime = System.currentTimeMillis();
                        if (task.time <= curTime) {
                            // 时间到了，执行任务
                            task.runnable.run();
                        } else {
                            // 时间没到，将任务放回队列
                            queue.put(task);
                            locker.wait(task.time - curTime);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

}

public class ThreadTest7 {
    public static void main(String[] args) {
        MyTimer timer = new MyTimer();
        timer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello1");
            }
        }, 1000);
        timer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello3");
            }
        }, 3000);
        timer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello4");
            }
        }, 4000);
        timer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello2");
            }
        }, 2000);

        System.out.println("hello0");
    }
}
