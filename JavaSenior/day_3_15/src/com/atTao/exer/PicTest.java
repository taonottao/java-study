package com.atTao.exer;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/18 19:57
 */
public class PicTest {

    //图片的加密
    @Test
    public void test1(){

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream("迦南.png");
            fos = new FileOutputStream("迦南secret.png");

            byte[] buffer = new byte[20];
            int len;
            while((len = fis.read(buffer)) != -1){

                //字节数组进行修改
                //错误的
    //            for(byte b : buffer){
    //                b = (byte) (b^5);
    //            }
                //正确的
                for(int i = 0; i < len; i++){
                    buffer[i] = (byte) (buffer[i] ^ 5);
                }

                fos.write(buffer,0,len);
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
//            fis.close();
        }


    }


    //图片的解密
    @Test
    public void test2(){

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream("迦南secret.png");
            fos = new FileOutputStream("迦南3.png");

            byte[] buffer = new byte[20];
            int len;
            while((len = fis.read(buffer)) != -1){

                //字节数组进行修改
                //错误的
                //            for(byte b : buffer){
                //                b = (byte) (b^5);
                //            }
                //正确的
                for(int i = 0; i < len; i++){
                    buffer[i] = (byte) (buffer[i] ^ 5);
                }

                fos.write(buffer,0,len);
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
//            fis.close();
        }


    }

}
