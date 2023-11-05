package Demo5;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest1 {
    static int num = 0;
    static Object lock = new Object();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Runnable task1 = new Task1();
        Runnable task2 = new Task2();
        pool.execute(task1);
        pool.execute(task2);
    }

    static class Task1 implements Runnable {

        @Override
        public void run() {

            while (num < 100) {
                synchronized (lock) {
                    while (num % 2 == 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": " + num++);
                    lock.notifyAll();
                }
            }
        }
    }

    static class Task2 implements Runnable {

        @Override
        public void run() {

            while (num < 100) {
                synchronized (lock) {
                    while (num % 2 == 1) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": " + num++);
                    lock.notifyAll();
                }
            }
        }
    }



}
