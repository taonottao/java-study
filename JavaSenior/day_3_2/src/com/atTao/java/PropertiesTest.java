package com.atTao.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/6 14:50
 */
public class PropertiesTest {

    //Properties：常用来处理配置文件。key和value都是String类型
    public static void main(String[] args){
        FileInputStream fis = null;
        try {
            Properties pros = new Properties();

            fis = new FileInputStream("jdbc.properties");
            pros.load(fis);//加载流对应的文件

            String name = pros.getProperty("name");
            String password = pros.getProperty("password");

            System.out.println("name="+name + ", password="+password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(fis != null){

                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }
}
