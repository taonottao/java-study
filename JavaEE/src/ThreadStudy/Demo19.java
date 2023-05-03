package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/3 16:42
 */

//通过Singleton这个类来实现单例模式.
class Singleton{
    private static Singleton instance = new Singleton();

    private Singleton(){

    }

    public static Singleton getInstance(){
        return instance;
    }

}

public class Demo19 {
    public static void main(String[] args) {
        Singleton instance = Singleton.getInstance();
    }
}
