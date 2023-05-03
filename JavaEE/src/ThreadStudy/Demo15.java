package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 19:50
 */
class Counter {
    //这个变量就是两个线程要去自增的变量
    public int count;

//    synchronized public void increase(){
//        count++;
//    }
    synchronized public void increase(){
        synchronized(this){
            count++;
        }
    }

    public static void  func(){
        synchronized(Counter.class){

        }
    }
    synchronized public static void  func1(){

    }
}

public class Demo15 {
    private static Counter counter = new Counter();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() ->{
            for (int i = 0; i < 50000; i++) {
                counter.increase();
            }
        });
        Thread t2 = new Thread(() ->{
            for (int i = 0; i < 50000; i++) {
                counter.increase();
            }
        });

        t1.start();
        t2.start();

        //必须在t1 t2 都执行完了之后, 在打印 count 结果
        //否则, main 和 t1 t2 之间都是并发的关系,导致t1 t2 还没执行完,就先执行了下面的打印操作
        t1.join();
        t2.join();

        //在 main 中打印一下两个线程自增完之后,得到的 count 结果
        System.out.println(counter.count);
    }
}
