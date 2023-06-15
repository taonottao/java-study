package old;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:13
 */
public class Framework {
    private Bottom bottom;

    public Framework(int size){
        bottom = new Bottom(size);
    }

    public void init(){
        // 依赖底盘
        bottom.init();
    }
}
