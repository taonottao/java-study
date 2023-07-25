package Demo4;

import java.lang.reflect.WildcardType;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/23 18:39
 */

class MyBlockingQueue {
    int[] items = new int[1000];

    // 约定 [head, tail) 区间为有效元素
    volatile private int head = 0;
    volatile private int tail = 0;
    volatile private int size = 0;

    // 添加元素
    synchronized public void put(int val) throws InterruptedException {
        while (size == items.length) {
            // 队列满了，添加失败
            this.wait();
        }
        items[size] = val;
        size++;
        tail++;
        if (tail == items.length) {
            tail = 0;
        }
        this.notify();
    }

    // 取元素
    synchronized public Integer take() throws InterruptedException {
        while (size == 0) {
            this.wait();
        }

        int ret = items[head];
        head++;
        if (head == items.length) {
            head = 0;
        }

        size--;
        this.notify();
        return ret;
    }
}

public class ThreadTest5 {
    public static void main(String[] args) {
        MyBlockingQueue queue = new MyBlockingQueue();
        // 消费者
        Thread t1 = new Thread(() -> {
            while (true) {
                int val = 0;
                try {
                    val = queue.take();
                    System.out.println("消费元素：" + val);
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();

        // 生产者
        Thread t2 = new Thread(() -> {
            int val = 0;
            while (true) {
                try {
                    queue.put(val);
                    System.out.println("生产元素：" + val);
                    val++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t2.start();
    }
}
