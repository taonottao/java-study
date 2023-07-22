package Demo3;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/22 17:37
 */
public class ThreadTest3 {

    public static void main(String[] args) throws InterruptedException {
        Object obj = new Object();

        System.out.println("wait 运行之前");
        synchronized (obj){
            obj.wait();
        }
        System.out.println("wait 运行之后");
    }

}
