package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/3 15:09
 */
public class Demo17 {
    public static void main(String[] args) throws InterruptedException {
        Object object = new Object();
        synchronized (object) {
            System.out.println("wait 前");
            object.wait();
            System.out.println("wait 后");
        }
    }
}
