package Demo4;

/**
 * 单例模式 - 饿汉式
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/7/23 8:17
 */

class Singleton {
    // 唯一实例
    private static Singleton instance = new Singleton();

    // 禁止外部去 new 实例
    private Singleton(){

    }

    // 获取到实例的方法
    public static Singleton getInstance() {
        return instance;
    }
}

public class ThreadTest1 {
    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();

        System.out.println(s1 == s2);
    }

}
