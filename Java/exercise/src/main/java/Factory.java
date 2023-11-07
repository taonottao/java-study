/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/8/30 21:52
 */

abstract class Food {
    public abstract void creat();
}

class cake extends Food {

    @Override
    public void creat() {
        System.out.println("蛋糕");
    }
}

class noddles extends Food {

    @Override
    public void creat() {
        System.out.println("面条");
    }
}

public class Factory {
    public static Food create(String name) {
        Food food = null;
        if ("蛋糕".equals(name)) {
            food = new cake();
        } else if ("面条".equals(name)) {
            food = new noddles();
        }
        return food;
    }
}
