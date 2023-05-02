package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 11:26
 */
class MyThread2 extends Thread{
    @Override
    public void run() {
        while (true){
            System.out.println("hello thread");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public class Demo2 {
    public static void main(String[] args) {
        Thread t = new MyThread2();
        t.start();

        while (true){
            System.out.println("hello main");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
