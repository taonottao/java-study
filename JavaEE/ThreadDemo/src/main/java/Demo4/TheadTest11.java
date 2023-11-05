package Demo4;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/8/15 15:06
 */
public class TheadTest11 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int sum = 0;
                for (int i = 1; i <= 1000; i++) {
                    sum += i;
                }
                return sum;
            }
        };

        FutureTask<Integer> futureTask = new FutureTask<>(callable);
        Thread t = new Thread(futureTask);
        t.start();
        System.out.println(futureTask.get());
    }
}
