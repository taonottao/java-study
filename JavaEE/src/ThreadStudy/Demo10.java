package ThreadStudy;

import java.util.Queue;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 14:59
 */
public class Demo10 {
    private static boolean isQuit = false;
    public static void main(String[] args) {
        Thread t = new Thread(() ->{
            while (!isQuit){
                System.out.println("hello thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();

        //只要把isQuit设为true,此时这个循环就退出了,进一步的run就执行完了,再进一步就是线程结束了
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        isQuit = true;
        System.out.println("终止t线程!");
    }
}
