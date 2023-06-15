package old;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:26
 */
public class Bottom {

    private Tire tire;

    public Bottom(int size){
        tire = new Tire(size);
    }

    public void init(){
        // 依赖轮胎
        tire.init();
    }
}
