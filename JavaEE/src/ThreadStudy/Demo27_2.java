package ThreadStudy;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/8 10:04
 */

class MyTask2 implements Comparable<MyTask2> {
    //要执行的任务
    private Runnable runnable;

    //多久后执行  毫秒
    private long time;

    public MyTask2(Runnable runnable, long time){
        this.runnable = runnable;
        this.time = time;
    }

    //获取执行任务得时间
    public long  getTime(){
        return time;
    }

    //执行任务
    public void run(){
        runnable.run();
    }


    @Override
    public int compareTo(MyTask2 o) {
        return (int) (this.getTime() - o.getTime());
    }
}

class MyTimer2{
    //扫描线程
    Thread t = null;

    //存放任务的数据结构,用优先级队列
    PriorityBlockingQueue<MyTask2> queue = new PriorityBlockingQueue<>();

    public MyTimer2(){
        t = new Thread(() -> {
            while (true){
                try {
                    synchronized (this) {
                        MyTask2 myTask2 = queue.take();
                        long curTime = System.currentTimeMillis();
                        if(curTime < myTask2.getTime()){//没到执行任务的时间
                            queue.put(myTask2);
                                this.wait(myTask2.getTime() - curTime);
                        }else {
                            //时间到了,执行任务
                            myTask2.run();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }

    //添加任务
    public void schedule(Runnable runnable, long after){
        MyTask2 myTask2 = new MyTask2(runnable, after + System.currentTimeMillis());
        queue.put(myTask2);
        synchronized (this) {
            this.notify();
        }
    }
}

public class Demo27_2 {
    public static void main(String[] args) {
        MyTimer2 myTimer2 = new MyTimer2();
        myTimer2.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务1");
            }
        }, 1000);
        myTimer2.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务2");
            }
        }, 2000);
        myTimer2.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("任务3");
            }
        }, 500);
    }
}
