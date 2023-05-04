package ThreadStudy;


/**
 * 懒汉式
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/3 17:26
 */

class Singleton2{
    private static volatile Singleton2 instance = null;

    private Singleton2(){

    }

    public static Singleton2 getInstance(){
        if(instance == null){
            synchronized (Singleton2.class) {
                if(instance == null){
                    instance = new Singleton2();
                }
            }
        }

        return instance;
    }
}

public class Demo20 {
    public static void main(String[] args) {
        Singleton2 instance = Singleton2.getInstance();
    }
}
