package com.atTao.java2;

import com.atTao.java1.Person;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 获取当前运行时类的属性结构
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/21 21:09
 */
public class FieldTest {

    @Test
    public void test1(){
        Class clazz = Person.class;

        //获取属性结构
        //getFields():获取当前运行时类及其父类中声明为public访问权限的属性
        Field[] fields = clazz.getFields();
        for(Field f : fields){
            System.out.println(f);
        }

        System.out.println("*******************************");

        //getDeclaredFields():获取当前运行时类中声明的所有属性。（不包含父类中声明的属性）
        Field[] fields1 = clazz.getDeclaredFields();
        for(Field f : fields1){
            System.out.println(f);
        }
    }

    //权限修饰符 数据类型 变量名
    @Test
    public void test2(){
        Class clazz = Person.class;
        Field[] fields1 = clazz.getDeclaredFields();
        for(Field f : fields1){
            //1.权限修饰符
            int modifier = f.getModifiers();
//            System.out.println(modifier);
            System.out.print(Modifier.toString(modifier) + "\t");
            //2.数据类型
            Class type = f.getType();
            System.out.print(type.getName() + "\t");
            //3.变量名
            String fName = f.getName();
            System.out.print(fName);


            System.out.println();
        }

    }

}
