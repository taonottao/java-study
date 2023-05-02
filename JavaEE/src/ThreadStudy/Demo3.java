package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 13:40
 */
//Runnable 就是在描述一个"任务"
class MyRunnable implements Runnable{

    @Override
    public void run() {
        System.out.println("hello");
    }
}

public class Demo3 {
    public static void main(String[] args) {
        Thread t= new Thread(new MyRunnable());
        t.start();
    }
}
