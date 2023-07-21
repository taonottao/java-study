package Demo1;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/20 9:18
 */
public class ThreadTest4 {
    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("匿名内部类，启动！");
            }
        });

        t.start();
    }
}
