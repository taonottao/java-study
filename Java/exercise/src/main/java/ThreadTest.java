import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/8/12 9:24
 */
public class ThreadTest {
    static AtomicInteger i = new AtomicInteger(0);
    public static void main(String[] args) {
        Object locker = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (locker) {
                while (i.get() <= 100){
                    System.out.println("线程" + Thread.currentThread().getName() + ": " + i.getAndIncrement());
                    locker.notify();
                    try {
                        locker.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread t2 = new Thread(() -> {
            synchronized (locker) {
                while (i.get() <= 100){
                    System.out.println("线程" + Thread.currentThread().getName() + ": " + i.getAndIncrement());
                    locker.notify();
                    try {
                        locker.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.start();
        t2.start();
    }
}
