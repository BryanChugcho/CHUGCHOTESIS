package VideoChatServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import org.json.JSONException;

/**
 *
 * @author Darkassesine
 */
public class VideochatServer {

    

    public static void main(String[] args) throws SocketException, IOException, JSONException {
        UdpServer udpServer = new UdpServer();
        udpServer.runPackageReception();
    }
}
