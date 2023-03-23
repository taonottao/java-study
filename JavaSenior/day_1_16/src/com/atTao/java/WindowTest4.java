package com.atTao.java;

/**
 * 使用同步方法解决继承Thread类的线程安全问题
 *
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/1/17 10:26
 */
class Window4 extends Thread {

    private static int ticket = 100;

    @Override
    public void run() {

        while (true) {

            show();
            if(ticket == 0){
                break;
            }

        }

    }
    private static synchronized void show(){//同步监视器：Window4.class
//    private synchronized void show(){//同步监视器：t1 t2 t3。错误
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


public class WindowTest4 {
    public static void main(String[] args) {
        Window4 t1 = new Window4();
        Window4 t2 = new Window4();
        Window4 t3 = new Window4();

        t1.setName("窗口一");
        t2.setName("窗口二");
        t3.setName("窗口三");

        t1.start();
        t2.start();
        t3.start();

    }
}