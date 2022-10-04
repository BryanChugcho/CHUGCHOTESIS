package VideoChatServer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * @author Darkassesine
 */
public class UdpServer {

    private DatagramSocket datagramSocket;
    private byte[] buffer;
    private ArrayList<String> clientsIp;

    public UdpServer() throws SocketException {
        datagramSocket = new DatagramSocket(4244);
        buffer = new byte[1024 * 64];
        clientsIp = new ArrayList<String>();
        
    }
  int PUERTO = 4244;  
  int i = 1;
public void runPackageReception() throws IOException, JSONException {
        while (true) {
            System.out.println("ipsss" + clientsIp);
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);
            JSONObject message = new JSONObject(new String(datagramPacket.getData(), 0, datagramPacket.getLength()));
            String senderIp = datagramPacket.getAddress().toString().substring(1);
            String por = datagramPacket.getAddress().toString();
            System.out.println("datagram packet" + por);
            
            System.out.println("ip"+ datagramPacket.getAddress());
            if (!clientsIp.contains(senderIp)) {
                clientsIp.add(senderIp);
            }
            
            sendPackageToClients(message, senderIp);
            
        }
    }

    private void sendPackageToClients(JSONObject message, String senderIp)
    {
        for (int i = 0; i < clientsIp.size(); i++) {
            if (clientsIp.get(i).contains(senderIp)) {
                continue;
            }
            
            try {
                DatagramPacket datagramPacket = new DatagramPacket(message.toString().getBytes(), 
                message.toString().getBytes().length,InetAddress.getByName(clientsIp.get(i)), PUERTO);
                datagramSocket.send(datagramPacket);
                System.out.println("probando" + InetAddress.getByName(clientsIp.get(i)));
            } catch (IOException ex) {
                System.out.println(ex.getStackTrace());
            }
        }
    }
}