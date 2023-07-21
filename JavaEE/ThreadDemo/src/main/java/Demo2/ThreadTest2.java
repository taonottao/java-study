package Demo2;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/20 10:34
 */
public class ThreadTest2 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("t, 启动！");
        });

        t.start();

        try {
            // 上面的 t 线程没有任何的循环和 sleep()，意味着代码会迅速执行完毕
            // main 线程如果 sleep 结束，此时的 t 基本上就是已经执行完了的状态
            // 此时虽然 t 对象还在，但是在系统中对应的线程已经结束了。
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(t.isAlive());
    }
}
