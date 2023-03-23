package com.atTao.java;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 测试FileInputStream和FileOutputStream的使用
 *
 * 结论：
 * 1.对于文本文件（.txt,.java,.c,.cpp），使用字符流处理
 * 2.对于非文本文件(.jpg,.mp3,.mp4,.avi,.doc,.ppt,...)，使用字节流处理
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/18 0:15
 */
public class FileInputOutputStreamTest {

    //使用FileInputStream处理文本文件，可能出现乱码
    @Test
    public void testFileInputStream() {

        FileInputStream fis = null;
        try {
            File file = new File("hello.txt");

            fis = new FileInputStream(file);

            byte[] buffer = new byte[5];
            int len;
            while((len = fis.read(buffer)) != -1){

                String str = new String(buffer,0,len);
                System.out.print(str);

            }
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

    @Test
    public void test(){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File srcFile = new File("迦南.png");
            File destFile = new File("迦南1.png");

            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);

            byte[] b = new byte[5];
            int len;
            while((len = fis.read(b)) != -1){
                fos.write(b,0,len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }




    }

    //指定路径下文件的复制
    public void copyFile(String srcPath, String destPath){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File srcFile = new File(srcPath);
            File destFile = new File(destPath);

            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);

            byte[] b = new byte[1024];
            int len;
            while((len = fis.read(b)) != -1){
                fos.write(b,0,len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(fos != null)
                    fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    public void testCopyFile(){
        long start = System.currentTimeMillis();
        String srcPath = "D:\\Download\\qq\\冷负荷计算.flv";
        String destPath = "D:\\Download\\qq\\冷负荷计算1.flv";

//        String srcPath = "hello.txt";
//        String destPath = "hello3.txt";

        copyFile(srcPath, destPath);
        long end = System.currentTimeMillis();

        System.out.println("复制操作花费的时间为：" + (end - start));//401
    }

}
