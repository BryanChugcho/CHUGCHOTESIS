
package CHAT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
/**
 *
 * @author Darkassesine
 */
public class NetworkTools {

    public static void sendData(String data, InetAddress address, int port, DatagramSocket socket) throws IOException {
        byte[] dataToSend = data.getBytes(); // Converts the message from String to array of bytes
        DatagramPacket packet = new DatagramPacket(dataToSend, dataToSend.length, address, port);
        socket.send(packet);
        System.out.println("puero" + port);
        
    }

    public static String receiveData(DatagramSocket socket) throws IOException {
        DatagramPacket packet = null;
        byte[] msg = new byte[200];
        Arrays.fill(msg, (byte) 0); //delete previous message
        packet = new DatagramPacket(msg, 200);
        socket.receive(packet);
        System.out.println("soket"+ socket.getPort());
        System.out.println("puerto " +packet.getPort());
        return new String(packet.getData(), 0, packet.getLength()) + "#" + packet.getAddress() + "#" + packet.getPort();
    }
}
