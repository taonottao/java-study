package Demo2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/11/8 11:15
 */
public class MyReflect {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class clazz = Class.forName("Demo2.Student");

//        Constructor[] constructors = clazz.getConstructors();
//        for (Constructor constructor : constructors) {
//            System.out.println(constructor);
//        }

//        Constructor[] constructors = clazz.getDeclaredConstructors();
//        for (Constructor constructor : constructors) {
//            System.out.println(constructor);
//        }

//        Constructor con = clazz.getDeclaredConstructor(String.class);
//        System.out.println(con);`
        Constructor con1 = clazz.getDeclaredConstructor(String.class, int.class);
        con1.setAccessible(true);
        Student stu = (Student) con1.newInstance("张三", 18);
        System.out.println(stu);
    }

}