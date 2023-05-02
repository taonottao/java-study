package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 16:15
 */
public class Demo13 {
    public static void main(String[] args) {
        Thread t1 = new Thread(){
            @Override
            public void run() {
//                System.out.println(Thread.currentThread().getName());
                //此时的this指向的就是当前Thread的实例
                System.out.println(this.getName());
            }
        };
        t1.start();
//        System.out.println(Thread.currentThread().getName());

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                //此处的this指向的不是Thread类型,而是runnable,而runnable 只是一个单纯 的任务,没有那么属性
//                System.out.println(this.getName());
                System.out.println(Thread.currentThread().getName());
            }
        });
        t2.start();
    }
}
