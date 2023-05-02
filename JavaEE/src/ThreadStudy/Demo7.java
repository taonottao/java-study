package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 14:05
 */
public class Demo7 {
    private static final long COUNT = 10_0000_0000;

    public static  void serial(){
        //记录程序执行时间
        long beg = System.currentTimeMillis();
        long a = 0;
        for (int i = 0; i < COUNT; i++) {
            a++;
        }
        long b = 0;
        for (int i = 0; i < COUNT; i++) {
            b++;
        }
        long end = System.currentTimeMillis();
        System.out.println("消耗时间:" + (end - beg) + "ms");
    }

    public static void concurrency() throws InterruptedException {
        long beg = System.currentTimeMillis();
        Thread t1 = new Thread(() -> {
            long a = 0;
            for (int i = 0; i < COUNT; i++) {
                a++;
            }
        });
        t1.start();
        Thread t2 = new Thread(() ->{
            long b = 0;
            for (int i = 0; i < COUNT; i++) {
                b++;
            }
        });

        //此处不能直接这么记录结束时间,别忘了,现在这个求时间戳的代码是在main线程中
        //main和t1,t2之间是并发执行的关系,此处t1 和 t2还没执行完,这里就开始记录结束时间了.这显然是不准确的
        //正确做法是让main线程等待t1和t2跑完了,再来记录时间.
        //join效果就是等待线程结束.t1.join就是让main 线程等待t1结束,t2.join就是让main 线程等待t2结束
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        System.out.println("消耗时间:" + (end - beg) + "ms");
    }

    public static void main(String[] args) throws InterruptedException {
//        serial();
        concurrency();
    }
}
