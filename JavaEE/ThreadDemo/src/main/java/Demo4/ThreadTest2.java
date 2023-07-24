package Demo4;

/**
 * 单例模式 - 懒汉式
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/23 8:25
 */

class SingletonLazy {
    volatile private static SingletonLazy instance = null;

    public static SingletonLazy getInstance() {
        // 判断是否要加锁
        // 如果不是 null，说明已经是线程安全的了，就不需要加锁了
        // 如果是 null，就可能有线程安全问题，需要加锁。
        if(instance == null){
            synchronized (SingletonLazy.class){
                if (instance == null) {
                    instance = new SingletonLazy();
                }
            }
        }

        return instance;
    }

    private SingletonLazy(){

    }
}

public class ThreadTest2 {

    public static void main(String[] args) {
        SingletonLazy s1 = SingletonLazy.getInstance();
        SingletonLazy s2 = SingletonLazy.getInstance();
        System.out.println(s1 == s2);
    }

}
