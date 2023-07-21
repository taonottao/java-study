package Demo1;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/20 9:27
 */
public class ThreadTest5 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("lambda表达式，启动！");
        });

        t.start();
    }
}
