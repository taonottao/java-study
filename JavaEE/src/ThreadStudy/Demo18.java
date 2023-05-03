package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/3 16:03
 */
public class Demo18 {
    public static Object locker =  new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            //进行 wait
            synchronized (locker){
                System.out.println("wait 之前");
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("wait 之后");
            }
        });
        t1.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t2 = new Thread(() -> {
            //进行 notify
            synchronized (locker){
                System.out.println("notify 之前");
                locker.notify();
                System.out.println("notify 之后");
            }
        });
        t2.start();
    }
}
