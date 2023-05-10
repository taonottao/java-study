package ThreadStudy;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/11 6:39
 */
public class Demo31 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 使用Callable 来计算 1 + 2 + 3 + ... + 1000
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

        Integer result = futureTask.get();
        System.out.println(result);
    }
}
