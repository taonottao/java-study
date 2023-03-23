package com.atTao.java;

/**
 * 使用同步代码快解决继承Thread类的方式的线程安全问题。
 *
 *
 * 例子：创建3个窗口卖票，总票数为100张,使用继承Thread类的方式
 *
 * 说明：在继承Thread类创建多线程的方式中，慎用this充当同步监视器，考虑使用当前类充当同步监视器。
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/1/15 23:57
 */
class Window2 extends  Thread{

    private static int ticket = 100;
    private static Object obj = new Object();

    @Override
    public void run() {

        while(true) {

//            synchronized (obj) {
              synchronized (Window2.class){
           // synchronized (this) { 错误的方式：this代表着t1、t2、t3三个对象
                if (ticket > 0) {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println(getName() + ":卖票，票号为：" + ticket);
                    ticket--;
                } else {
                    break;
                }

            }
        }

    }
}



public class WindowTest2 {
    public static void main(String[] args) {
        Window2 t1 = new Window2();
        Window2 t2 = new Window2();
        Window2 t3 = new Window2();

        t1.setName("窗口一");
        t2.setName("窗口二");
        t3.setName("窗口三");

        t1.start();
        t2.start();
        t3.start();

    }
}