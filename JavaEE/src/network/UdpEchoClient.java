package network;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/13 18:52
 */
// UDP 版本的回显客户端
public class UdpEchoClient {
    private DatagramSocket socket = null;
    private String serverIp = null;
    private int serverPort = 0;

    // 一次通信需要两个ip, 两个端口
    // 客户端的 ip 是 127.0.0.1 已知
    // 客户端的  port 是系统自动分配的
    // 服务器的 ip 和 端口 也需要告诉客户端,  才能顺利把消息发给服务器
    public UdpEchoClient(String serverIp, int serverPort) throws SocketException {
        socket = new DatagramSocket();
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void start(){
        System.out.println("客户端启动 !");
        while(true){
            // 1. 从控制台读取要发送的数据
            // 2. 构造成 UDP 请求, 并发送
            // 3. 读取服务器的 UDP 响应, 并解析
            // 4. 吧解析好的结果显示出来
        }
    }
}
