package com.atTao.java;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/8 20:45
 */
public class SubOrder extends Order<Integer>{//subOrder<T>:不是泛型类

    public static <E> List<E> copyFromArrayToList(E[] arr){

        ArrayList<E> list = new ArrayList<>();

        for (E e : arr){
            list.add(e);
        }
        return list;
    }

}
