package ThreadStudy;

import java.util.concurrent.Semaphore;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/11 10:40
 */
public class Demo33 {
    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);
//        semaphore.acquire(2); //一次申请两个资源
        semaphore.acquire();
        System.out.println("执行一次 P 操作");
        semaphore.acquire();
        System.out.println("执行一次 P 操作");
        semaphore.acquire();
        System.out.println("执行一次 P 操作");
        semaphore.acquire();
        System.out.println("执行一次 P 操作");

        semaphore.release();
    }
}
