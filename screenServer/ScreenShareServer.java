package screenServer;

import java.io.IOException;
import java.net.SocketException;
import org.json.JSONException;

/**
 *
 * @author Darkasessine
 * */
public class ScreenShareServer
{

    public static void main(String[] args) throws SocketException, IOException, JSONException
    {
        UdpServer udpServer = new UdpServer();
        udpServer.runPackageReception();
    }
}
