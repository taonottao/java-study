package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 14:31
 */
public class Demo8 {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true){
                System.out.println("hello thread1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "thread t1");
        t1.start();

        Thread t2 = new Thread(() ->{
            while (true){
                System.out.println("hello thread2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },  "thread t2");
        t2.start();
    }
}
