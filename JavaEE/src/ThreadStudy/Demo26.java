package ThreadStudy;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/7 22:11
 */
public class Demo26 {
    public static void main(String[] args) {
        System.out.println("程序启动");
        //这个Timer类就是标准库的定时器
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("运行定时任务1");
            }
        }, 3000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("运行定时任务2");
            }
        }, 2000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("运行定时任务3");
            }
        }, 1000);
    }
}
