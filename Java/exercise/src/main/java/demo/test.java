package demo;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/11/6 11:45
 */
public class test {
    public static void main(String[] args) {
        // 赵大明星唱一首歌

        Star proxy = ProxyUtil.createProxy(new BigStar("鸡哥"));

        String ret = proxy.sing("姬霓太美");
        System.out.println(ret);

    }
}
