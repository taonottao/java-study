package atTao.java;

/**
 *例子：创建3个窗口卖票，总票数为100张,使用实现runnable接口的方式
 *
 *存在线程安全问题，待解决
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/1/16 12:03
 */

class Window1 implements Runnable{

    private int ticket = 100;

    @Override
    public void run() {
        while(true){
            if(ticket > 0){
                System.out.println(Thread.currentThread().getName()+":卖票，票号为："+ticket);
                ticket--;
            }else{
                break;
            }
        }
    }
}

public class WindowTest1 {
    public static void main(String[] args) {
        Window1 w1 = new Window1();

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