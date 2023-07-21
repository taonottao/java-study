package Demo2;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/20 9:46
 */
public class ThreadTest1 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            while (true) {
                System.out.println("线程，启动！");
                try {
                    Thread.sleep(1000);// 运行大此方法时，等待1000ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "我的线程");

        t.start();
    }
}
