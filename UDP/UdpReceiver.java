package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.SourceDataLine;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Darkassesine
 */
public class UdpReceiver extends Thread
{

    private DatagramSocket datagramSocket;
    private byte[] buffer;
    private SourceDataLine sourceDataLine;

    public UdpReceiver(SourceDataLine sourceDataLine)
    {
        this.sourceDataLine = sourceDataLine;

        try
        {
            datagramSocket = new DatagramSocket(4243);
            buffer = new byte[1024*100];
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            getPackage();
        }
        catch (IOException ex)
        {
            System.err.println("error en la lectura del mensaje");
        }
    }

    private void getPackage() throws IOException
    {
        
        while (true)
        {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                
                String data = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                
                JSONObject message = null;
                try {
                    message = new JSONObject(data);
                } catch (JSONException ex) {
                    Logger.getLogger(UdpReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                byte[] bytes = Base64.getDecoder().decode(message.getString("audio"));
                sourceDataLine.write(bytes, 0, bytes.length);
            } catch (JSONException ex) {
                Logger.getLogger(UdpReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
