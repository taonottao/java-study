package ioc;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 9:27
 */
public class Tire {
    private int size = 20;
    private String color;

    public Tire(int size, String color){
        this.size =  size;
        this.color = color;
    }

    public void init(){
        System.out.println("执行了轮胎初始化方法, size:" + this.size + " color: "  + color);
    }
}
