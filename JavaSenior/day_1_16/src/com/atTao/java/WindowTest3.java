package com.atTao.java;

/**
 * 使用同步方法解决实现Runnable接口的线程安全问题
 *
 *
 * 关于同步方法的总结：
 * 1.同步方法仍然涉及到同步监视器，只是不需要我们显式的声明。
 * 2.非静态的同步方法，同步监视器是this
 *   静态的同步方法，同步监视器是当前类本身
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/1/17 9:57
 */
class Window3 implements Runnable {

    private int ticket = 100;

    @Override
    public void run() {
        while (true) {
            show();
            if(ticket == 0){
                break;
            }
        }
    }

    private synchronized void show(){//同步监视器：this
        if (ticket > 0) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(Thread.currentThread().getName() + ":卖票，票号为：" + ticket);
            ticket--;
        }
    }
}

public class WindowTest3 {
    public static void main(String[] args) {
        Window3 w1 = new Window3();

        Thread t1 = new Thread(w1);
        Thread t2 = new Thread(w1);
        Thread t3 = new Thread(w1);

        t1.setName("窗口1");
        t2.setName("窗口2");
        t3.setName("窗口3");

        t1.start();
        t2.start();
        t3.start();
    }
}
