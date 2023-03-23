package com.atTao.exer1;

import org.junit.Test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 创建该类的 5 个对象，并把这些对象放入 TreeSet 集合中（下一章：
 * TreeSet 需使用泛型来定义）
 * 分别按以下两种方式对集合中的元素进行排序，并遍历输出：
 * 1). 使 Employee 实现 Comparable 接口，并按 name 排序
 * 2). 创建 TreeSet 时传入 Comparator 对象，按生日日期的先后排序。
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/4 15:24
 */
public class EmployeeTest {

    //使用自然排序
    @Test
    public void test1(){
        TreeSet set = new TreeSet();

        Employee e1 = new Employee("Tom",18,new MyDate(2005,3,4));
        Employee e2 = new Employee("Mike",33,new MyDate(1990,2,16));
        Employee e3 = new Employee("Jerry",18,new MyDate(2005,1,10));
        Employee e4 = new Employee("LiHua",15,new MyDate(2008,9,20));
        Employee e5 = new Employee("Mary",45,new MyDate(1988,6,4));

        set.add(e1);
        set.add(e2);
        set.add(e3);
        set.add(e4);
        set.add(e5);

        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }



    }

    //使用定制排序
    @Test
    public void test2(){
        Comparator com = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if(o1 instanceof Employee && o2 instanceof Employee){
                    Employee e1 = (Employee) o1;
                    Employee e2 = (Employee) o2;

                    MyDate d1 = e1.getBirthday();
                    MyDate d2 = e2.getBirthday();

                    //方式一：
//                    int minusYear = d1.getYear() - d2.getYear();
//                    if (minusYear != 0){
//                        return minusYear;
//                    }
//
//                    int minusMonth = d1.getMonth() - d2.getMonth();
//                    if (minusMonth != 0){
//                        return minusMonth;
//                    }
//
//                    return d1.getDay() - d2.getDay();

                    return d1.compareTo(d2);
                }

                throw new RuntimeException("输入的数据类型不一致！");
            }


        };
        TreeSet set = new TreeSet(com);

        Employee e1 = new Employee("Tom",18,new MyDate(2005,3,4));
        Employee e2 = new Employee("Mike",33,new MyDate(1990,2,16));
        Employee e3 = new Employee("Jerry",18,new MyDate(2005,1,10));
        Employee e4 = new Employee("LiHua",15,new MyDate(2008,9,20));
        Employee e5 = new Employee("Mary",45,new MyDate(1988,6,4));

        set.add(e1);
        set.add(e2);
        set.add(e3);
        set.add(e4);
        set.add(e5);

        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
