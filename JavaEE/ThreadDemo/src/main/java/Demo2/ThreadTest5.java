package Demo2;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/21 11:46
 */
public class ThreadTest5 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            while (true){
                System.out.println("hello t");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        try {
            t.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("hello main");
    }
}
