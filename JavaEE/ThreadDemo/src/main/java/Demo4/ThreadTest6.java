package Demo4;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/24 10:36
 */
public class ThreadTest6 {
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        }, 2000);
    }
}
