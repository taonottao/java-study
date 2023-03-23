package com.atTao.exer;

import org.junit.Test;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/2/4 12:05
 */
public class StringDemo {

    /*
    将一个字符串进行反转。将字符串中指定位置部分进行反转。比如“abcdefg”反转为“abfedcg”
     */
    //方式一：转换为char[]
    public String reverse(String str, int startIndex, int endIndex){
        if(str != null){
            char[] arr = str.toCharArray();
            for(int i = startIndex, j = endIndex; i < j; i++, j--){
                char tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }
            return new String(arr);
        }
        return null;
    }

    //方式二：使用String的拼接
    public String reverse1(String str, int startIndex, int endIndex){
        if(str != null){
            //第一部分
            String reverseStr = str.substring(0, startIndex);
            //第二部分
            for(int i = endIndex; i >= startIndex; i--){
                reverseStr += str.charAt(i);
            }
            //第三部分
            reverseStr += str.substring(endIndex + 1);

            return  reverseStr;
        }

        return null;

    }

    //方式三：使用StringBuffer/StringBuilder替换String
    public String reverse2(String str, int startIndex, int endIndex){
        if(str != null){
            StringBuilder builder = new StringBuilder(str.length());
            //第一部分
            builder.append(str.substring(0,startIndex));
            //第二部分
            for(int i = endIndex; i >= startIndex; i--){
                builder.append(str.charAt(i));
            }
            //第三部分
            builder.append(str.substring(endIndex + 1));

            return builder.toString();
        }

        return null;

    }

    @Test
    public void testReverse(){
        String s1 = "abcdefg";
        String s2 =reverse2(s1, 2, 5);
        System.out.println(s2);

    }
}
