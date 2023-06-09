package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 15:08
 */
public class Demo11 {
    public static void main(String[] args) {
        Thread t = new Thread(() ->{
            while (!Thread.currentThread().isInterrupted()){
                System.out.println("hello  thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //当触发异常之后,立即退出循环
                    //做一些收尾工作
                    System.out.println("这是收尾工作");
                    break;
                }
            }
        });
        t.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //在主线程中,调用interrupt方法,来中断这个线程
        //t.interrupt 的意思就是让 t 线程被中断!!
        t.interrupt();
    }
}
