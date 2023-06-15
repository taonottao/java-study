package ioc;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:13
 */
public class Framework {
    private Bottom bottom;

    public Framework(Bottom bottom){
        this.bottom = bottom;
    }

    public void init(){
        System.out.println("执行了 framew");
        // 依赖底盘
        bottom.init();
    }
}
