package com.atTao.java1;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URL网络编程
 * 1.URL：统一资源定位符，对应着互联网的某一资源地址
 * 2.格式：
 * https://localhost:8080/video/BV1Kb411W75N/?p=629&spm_id_from=pageDriver
 * 协议    主机名    端口号 资源地址             参数列表
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/20 21:48
 */
public class URLTest {

    public static void main(String[] args) throws MalformedURLException {

        URL url = new URL("https://localhost:8080/video/BV1Kb411W75N/?p=629&spm_id_from=pageDriver");

//        public String getProtocol( ) 获取该URL的协议名
        System.out.println(url.getProtocol());
//        public String getHost( ) 获取该URL的主机名
        System.out.println(url.getHost());
//        public String getPort( ) 获取该URL的端口号
        System.out.println(url.getPort());
//        public String getPath( ) 获取该URL的文件路径
        System.out.println(url.getPath());
//        public String getFile( ) 获取该URL的文件名
        System.out.println(url.getFile());
//        public String getQuery( ) 获取该URL的查询名
        System.out.println(url.getQuery());


    }
}
