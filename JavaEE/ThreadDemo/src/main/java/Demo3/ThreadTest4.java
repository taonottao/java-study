package Demo3;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/22 18:17
 */
public class ThreadTest4 {

    public static void main(String[] args) throws InterruptedException {
        Object locker = new Object();

        Thread t1 = new Thread(() -> {
                try {
                    synchronized (locker) {
                        System.out.println("wait 之前");
                        locker.wait();
                        System.out.println("wait 之后");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        });

        t1.start();

        Thread.sleep(1000);

        Thread t2 = new Thread(() -> {
            synchronized (locker){
                System.out.println("notify 之前");
                locker.notify();
                System.out.println("notify 之后");
            }
        });

        t2.start();
    }

}
