package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/6 22:11
 */
public class Demo23 {
    public static void main(String[] args) {
        Object locker1 = new Object();
        Object locker2 = new Object();
        //解决死锁方法,规定只能先拿locker1,再拿locker2.
        Thread t1 = new Thread(() ->{
                synchronized (locker1){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (locker2){
                        System.out.println("t1拿到了两把锁");
                    }
                }
        });
        Thread t2 = new Thread(() ->{
                synchronized (locker1){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (locker2){
                        System.out.println("t2拿到了两把锁");
                    }
                }
        });

        t1.start();
        t2.start();

    }
}
