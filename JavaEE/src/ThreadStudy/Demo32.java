package ThreadStudy;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/11 6:57
 */
public class Demo32 {
    public static void main1(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        try{
            reentrantLock.lock();// 加锁

            if(cond1){
                reentrantLock.unlock();
                return;
            }
            if(cond2){
                reentrantLock.unlock();
                return;
            }
            if(cond3){
                reentrantLock.unlock();
                return;
            }

            throw new Exception();
        }finally{
            reentrantLock.unlock();// 解锁
        }

    }
    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        boolean result =  reentrantLock.tryLock();// 加锁
        try{
            if(result){
                // 和女神吃饭(需要考虑线程安全的操作)
            }else {
                // 啥都不做
            }
        }finally{
            if(result)
                reentrantLock.unlock();// 解锁
        }

    }
}
