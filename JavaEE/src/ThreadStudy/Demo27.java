package ThreadStudy;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/7 22:26
 */

//使用这个类来表示一个定时器中的任务
class MyTask implements Comparable<MyTask> {
    //要执行的任务内容
    private Runnable runnable;
    //任务啥时候执行(使用毫秒时间戳表示)
    private long time;

    public MyTask(Runnable runnable, long time){
        this.runnable = runnable;
        this.time = time;
    }

    //获取当前任务的时间
    public long getTime() {
        return time;
    }

    //执行任务
    public void run(){
        runnable.run();
    }

    @Override
    public int compareTo(MyTask o) {
        //返回 小于0  大于0  0
        //this比o小, 返回 <0
        //this比o大, 返回 >0
        //this和o相同, 返回 0
        // 当前 要实现 的效果是,队首元素是时间最小的任务
        //这俩是谁减谁,千万不要记 ,而是试试就知道了
        //要么是this.time - o.time, 要么是 o.time - this.time
        return (int) (this.time - o.time);
    }
}

class MyTimer{
    //扫描线程
    private Thread t = null;

    //有一个阻塞优先级队列,来保存任务
    private PriorityBlockingQueue<MyTask> queue = new PriorityBlockingQueue<>();

    public MyTimer(){
        t = new Thread(() -> {
            while (true){
                try {
                    //取出队首元素,检查看看队首元素任务是否到时间了.
                    //如果时间没到,就把任务塞回队列里去
                    //如果时间到了,就把任务进行执行.
                    synchronized (this) {
                        MyTask myTask = queue.take();
                        long curTime = System.currentTimeMillis();
                        if(curTime < myTask.getTime()){
                            //还没到点,先不必执行
                            // 比如 现在是13:00, 取出来的任务是14:00执行
                            queue.put(myTask);
                            //在put之后,进行一个wait
                                this.wait(myTask.getTime() - curTime);
                        }else {
                            //时间到了,执行任务
                            myTask.run();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /*
    指定两个参数
    1.任务内容
    2.任务在多少毫秒之后执行, 形如 1000
     */
    public void schedule(Runnable runnable, long after){
        //注意这里的时间上的换算
        MyTask task = new MyTask(runnable, System.currentTimeMillis() + after);
        queue.put(task);
        synchronized (this) {
            this.notify();
        }
    }
}

public class Demo27 {
    public static void main(String[] args) {
        MyTimer myTimer = new MyTimer();
        myTimer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务1");
            }
        }, 1000);
        myTimer.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务2");
            }
        }, 2000);
    }
}
