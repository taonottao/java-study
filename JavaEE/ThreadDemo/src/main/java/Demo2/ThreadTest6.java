package Demo2;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/21 12:18
 */
public class ThreadTest6 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
//            System.out.println("呵呵");
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 在线程启动之前，获取线程的状态 NEW
        System.out.println(t.getState());

        t.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(t.getState());
    }
}
