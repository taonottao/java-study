package Demo2;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/11/8 10:39
 */
public class MyReflect1 {
    public static void main(String[] args) throws ClassNotFoundException {

        // 第一种方式获取 class 对象
        // 全类名：包名 + 类名
        Class clazz = Class.forName("Demo2.Student");
        System.out.println(clazz);
        // 第二种方式
        Class clazz2 = Student.class;
        System.out.println(clazz2 == clazz);
        // 第三种方式
        Class clazz3 = new Student().getClass();
        System.out.println(clazz2 == clazz3);
    }
}
