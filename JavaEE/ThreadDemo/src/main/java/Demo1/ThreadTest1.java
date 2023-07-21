package Demo1;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/19 18:43
 */

class MyThread extends Thread{
    @Override
    public void run() {
        System.out.println("MyThread线程，启动！");
    }
    public void run1(){
        System.out.println("run1");
    }
}

public class ThreadTest1 {
    public static void main(String[] args) {
        Thread t = new MyThread();
        t.start();
        MyThread t2 = new MyThread();
        t2.start();
    }
}
