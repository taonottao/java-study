package Demo2;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/20 19:29
 */
public class ThreadTest3 {

    public static boolean isQuit = false;

    public static void main(String[] args) {
//        boolean isQuit = false;
        Thread t = new Thread(() -> {
            while (!isQuit) {
                System.out.println("t, 启动！");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("t, 结束！");
        });

        t.start();

        // 在主线程中修改 isQuit
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isQuit = true;
    }
}
