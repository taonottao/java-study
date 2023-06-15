package ioc;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:12
 */
public class Car {
    private Framework framework;

    public Car(Framework framework){
        this.framework = framework;
    }

    public void init(){
        System.out.println("执行了 Car");
        // 依赖车身
        framework.init();
    }

}
