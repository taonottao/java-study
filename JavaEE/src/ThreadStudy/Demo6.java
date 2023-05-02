package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/2 14:01
 */
public class Demo6 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> System.out.println("hello thread"));
        //start是一个特殊的方法,内部会在系统中创建线程
        t.start();
        //run只是一个普通的方法,描述了任务的内容,见Demo9
//        t.run();
    }
}
