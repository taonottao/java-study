package ioc;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:26
 */
public class Bottom {

    private Tire tire;

    public Bottom(Tire tire) {
        this.tire  = tire;
    }

    public void init(){
        System.out.println("执行bottom");
        // 依赖轮胎
        tire.init();
    }
}
