package com.atTao.exer2;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/14 22:08
 */
public class FileDemo {

    @Test
    public void test1() throws IOException {
        File file = new File("E:\\IO\\IO1\\hello.txt");
        //创建一个与file同目录下的另外一个文件，文件名为：haha.txt
        File file1 = new File(file.getParent(),"haha.txt");
        boolean newFile = file1.createNewFile();
        if(newFile){
            System.out.println("创建成功");
        }
    }
}
