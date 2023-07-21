package Demo1;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/20 9:15
 */
public class ThreadTest3 {
    public static void main(String[] args) {
        Thread t = new Thread(){
            @Override
            public void run() {
                System.out.println("匿名内部类，启动！");
            }
        };

        t.start();
    }
}
