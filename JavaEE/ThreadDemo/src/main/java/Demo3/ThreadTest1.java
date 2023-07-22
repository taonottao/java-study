package Demo3;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/21 19:18
 */

class Counter{
    private int count = 0;

    public void add(){
        count++;
    }

    public int getCount() {
        return count;
    }
}

public class ThreadTest1 {
    public static void main(String[] args) {
        Counter counter = new Counter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                counter.add();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                counter.add();
            }
        });

        t1.start();
        t2.start();

        // 等待两个线程执行结束，看看结果
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(counter.getCount());
    }
}
