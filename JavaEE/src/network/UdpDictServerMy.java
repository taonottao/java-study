package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/15 23:35
 */
public class UdpDictServerMy {
    private DatagramSocket socket = null;

    public UdpDictServerMy(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void start(){
        System.out.println("服务器启动!!");

        while(true){
            DatagramPacket requestPacket = new DatagramPacket(new byte[4096], 4096);
        }
    }

}
