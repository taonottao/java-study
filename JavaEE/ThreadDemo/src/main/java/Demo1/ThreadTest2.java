package Demo1;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/19 18:43
 */

class MyRunnable implements Runnable{
    @Override
    public void run() {
        while (true) {
            System.out.println("Runnable, 启动！");
        }
    }
}

public class ThreadTest2 {
    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();
        Thread t = new Thread(runnable);
        t.start();
    }
}
