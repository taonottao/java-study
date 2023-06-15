package ioc;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 10:27
 */
public class APP {
    public static void main(String[] args) {
        Tire tire = new Tire(15, "blue");
        Bottom bottom = new Bottom(tire);
        Framework framework = new Framework(bottom);
        Car car = new Car(framework);
        car.init();
    }
}
