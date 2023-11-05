package Demo5;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest2 {

    static int num = 0;
    static Object lock = new Object();

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            final int num = i;
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    print(num);
                }
            });
        }
    }

    public static void print(int i) {
        while (num < 100) {
            synchronized (lock) {
                while (num % 3 == i) {
                    System.out.println(Thread.currentThread().getName() + ": " + num++);
                }
            }
        }
    }

}
