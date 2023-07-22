package Demo3;

import java.util.Scanner;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/22 12:35
 */
public class ThreadTest2 {

    volatile public static int flag = 0;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (flag == 0){
                // flag 为 0 一直循环
            }
            System.out.println("t1, 循环结束");
        });

        Thread t2 = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入一个整数：");
            flag = scanner.nextInt();
        });

        t1.start();
        t2.start();
    }
}
