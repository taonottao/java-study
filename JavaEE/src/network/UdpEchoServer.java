package network;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/13 18:52
 */
// UDP 版本的回显服务器
public class UdpEchoServer {
    // 网络编程, 本质上是要操作网卡
    // 但是网卡不方便直接操作, 在操作系统内核中, 使用了一种特殊的叫做"socket"这样的文件来抽象表示网卡.
    // 因此进行网络通信,  势必需要先有一个 socket 对象.
    private DatagramSocket socket =  null;

    // 对于服务器来说, 创建 socket 对象的同时, 要让他绑定一个具体的端口号.
    // 服务器一定要关联上一个 具体的端口的!!!
    // 服务器是网络传输中被动的一方, 如果是操作系统随机分配的端口,
    // 此时客户端就不知道这个端口是啥了, 也就无法进行通信了.
    public UdpEchoServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void start() throws IOException {
        System.out.println("服务器启动!");
        // 服务器不是只给一个客户端服务就完了, 需要服务很多客户端
        while(true){
            // 只要有客户端过来, 就可以提供服务
            // 1. 读取客户端发来的请求是啥
            //  receive 方法的参数是一个输出型参数,
            //  需要先构造好个空白的 DatagramPacket 对象. 交给receive 来进行填充
            DatagramPacket requestPacket = new DatagramPacket(new byte[4096], 4096);
            socket.receive(requestPacket);
            // 此时这个 DatagramPacket 是一个特殊的对象, 并不方便直接处理.
            // 可以把这里包含的数据拿出来, 构成一个字符串
            String request = new String(requestPacket.getData(), 0, requestPacket.getLength());
            // 2. 根据请求计算响应, 由于此处是回显服务器, 响应和请求相同
            String response = process(request);
            // 3. 把响应写会到客户端, send 的参数也是DatagramPacket, 需要把这个packet对象构造好
            //    此处构造的响应对象, 不能是使用空的字节数组构造了, 而是使用响应数据来构造
            DatagramPacket responsePacket =  new DatagramPacket(response.getBytes(), response.getBytes().length,
                    requestPacket.getSocketAddress());
            socket.send(responsePacket);
        }
    }

    // 这个方法表示"根据请求计算响应"
    public String process(String request) {
        return request;
    }
}
