package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/7 8:24
 */
public class Demo24 {
    //有三个线程,分别只能打印A B C,控制三个线程固定按照ABC的顺序来打印
    public static void main(String[] args) throws InterruptedException {
//        Object locker1 = new Object();
        Object locker2 = new Object();
        Object locker3 = new Object();
        Thread t1 = new Thread(() -> {
//            synchronized (locker1) {
                System.out.println("A");
                synchronized (locker2){
                    locker2.notify();
                }
//            }
        });
        Thread t2 = new Thread(() -> {
            synchronized (locker2) {
                try {
                    locker2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("B");
                synchronized (locker3){
                    locker3.notify();
                }
            }
        });
        Thread t3 = new Thread(() -> {
            synchronized (locker3) {
                try {
                    locker3.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("C");
            }
        });

        t2.start();
        t3.start();
        Thread.sleep(100);
        t1.start();
    }
}
