package ThreadStudy;

import java.util.Queue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/4 20:55
 */

class MyBlockingQueue{
    // 保存数据的本体
    private int[] data = new int[1000];
    // 有效元素的个数
    private int size = 0;
    // 队首下标
    private int head = 0;
    // 队尾下标
    private int tail = 0;

    // 入队列
    public void put(int value) throws InterruptedException {
        synchronized (this){
            if(size == data.length){
                // 队列满了
//                return;
                this.wait();
            }
            // 把新的元素放到 tail 位置上
            data[tail++] = value;
            // 处理 tail 到达数组末尾的情况
            if(tail >= data.length){
                tail = 0;
            }

//        tail = tail % data.length;
            size++;
            // 如果入队列成功,则队列非空,于是就唤醒take中的阻塞等待
            this.notify();
        }

    }

    // 出队列
    public Integer  take() throws InterruptedException {
        synchronized (this){
            if(size == 0){
                // 如果队列为空,就返回一个 非法值
//                return null;
                this.wait();
            }

            // 取出head位置的元素
            int ret = data[head++];
            if(head >= data.length){
                head = 0;
            }
            size--;
            //take成功之后,就唤醒put中的等待
            this.notify();
            return ret;
        }
    }
}

public class Demo22 {
    private static MyBlockingQueue queue = new MyBlockingQueue();

    public static void main(String[] args) {

        //实现一个简单的生产者消费者模型
        Thread producer = new Thread(() ->{
            int num = 0;
            while (true){
                try {
                    System.out.println("生产了: " + num);
                    queue.put(num++);
                    //当生产者生产 的慢一些的时候, 消费者就得跟着生产者的步伐走
//                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        producer.start();

        Thread customer = new Thread(() ->{
            while (true){
                try {
                    int num = queue.take();
                    System.out.println("消费了: " + num);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        customer.start();

        // 简单验证这个队列是否能正确工作
//        MyBlockingQueue queue = new MyBlockingQueue();
//        queue.put(1);
//        queue.put(2);
//        queue.put(3);
//        queue.put(4);
//
//        int ret = queue.take();
//        System.out.println(ret);
//        ret = queue.take();
//        System.out.println(ret);
//        ret = queue.take();
//        System.out.println(ret);
//        ret = queue.take();
//        System.out.println(ret);
    }
}
