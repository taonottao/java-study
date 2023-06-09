package com.atTao.java1;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 实现TCP的网络编程
 * 例题2：客户端发送文件给服务端，服务端将文件保存在本地。
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/3/20 17:32
 */
public class TCPTest2 {
    /*
    这里涉及的异常，应该使用try-catch-finally处理
     */

    @Test
    public void client() throws IOException {

        Socket socket = new Socket("127.0.0.1",9090);

        OutputStream os = socket.getOutputStream();

        FileInputStream fis = new FileInputStream("迦南.png");

        byte[] buffer = new byte[1024];
        int len;
        while((len = fis.read(buffer)) != -1){
            os.write(buffer,0,len);
        }

        fis.close();
        os.close();
        socket.close();



    }

    @Test
    public void server() throws IOException {

        ServerSocket ss = new ServerSocket(9090);

        Socket socket = ss.accept();

        InputStream is = socket.getInputStream();

        FileOutputStream fos = new FileOutputStream("迦南3.png");

        byte[] buffer = new byte[1024];
        int len;
        while((len = is.read(buffer)) != -1){
            fos.write(buffer,0,len);
        }

        fos.close();
        is.close();
        socket.close();
        ss.close();




    }

}
