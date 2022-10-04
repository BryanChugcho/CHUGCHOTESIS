package screenServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Darkasessine
 */
public class UdpServer {

    private DatagramSocket datagramSocket;
    private byte[] buffer;
    private ArrayList<String> clientsIp;

    public UdpServer() throws SocketException {
        datagramSocket = new DatagramSocket(4245);
        buffer = new byte[1024 * 64];
        clientsIp = new ArrayList<String>();
    }

    public void runPackageReception() throws IOException, JSONException {
        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);

            JSONObject message = new JSONObject(new String(datagramPacket.getData(), 0, datagramPacket.getLength()));

            String senderIp = datagramPacket.getAddress().toString().substring(1);

            System.out.println(senderIp);

            if (!clientsIp.contains(senderIp)) {
                clientsIp.add(senderIp);
            }
            sendPackageToClients(message, senderIp);

        }
    }

    private void sendPackageToClients(JSONObject message, String senderIp) {
        for (int i = 0; i < clientsIp.size(); i++) {
            if (clientsIp.get(i).equals(senderIp)) {
                continue;
            }

            try {
                DatagramPacket datagramPacket = new DatagramPacket(message.toString().getBytes(), message.toString().getBytes().length, InetAddress.getByName(clientsIp.get(i)), 4245);
                datagramSocket.send(datagramPacket);
            } catch (IOException ex) {
                System.out.println(ex.getStackTrace());
            }
        }
    }
}
