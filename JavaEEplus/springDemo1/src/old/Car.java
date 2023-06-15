package old;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:12
 */
public class Car {
    private Framework framework;

    public Car(int size){
        framework = new Framework(size);
    }

    public void init(){
        // 依赖车身
        framework.init();
    }

}
