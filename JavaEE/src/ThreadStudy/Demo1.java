package ThreadStudy;

/**
 * 最基本的创建线程的办法
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 11:17
 */

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("hello thread");
    }
}

public class Demo1 {
    public static void main(String[] args) {
        Thread t = new MyThread();
        t.start();
    }
}
