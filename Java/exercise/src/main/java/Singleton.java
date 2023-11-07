
/**
 * 懒汉式
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/8/12 9:02
 */
public class Singleton {

    private volatile static Singleton instance = null;

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

}
