package ThreadStudy;

import java.util.Scanner;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 21:50
 */
public class Demo16 {
//    private static volatile int isQuit = 0;
    private static int isQuit = 0;

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            while (isQuit == 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("循环结束! t 线程退出!");
        });
        t.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入一个isQuit的值:");
        isQuit =  scanner.nextInt();
        System.out.println("main线程执行完毕!");
    }
}
