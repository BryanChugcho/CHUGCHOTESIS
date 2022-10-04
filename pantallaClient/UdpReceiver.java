package pantallaClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JPanel;
import org.json.JSONObject;

/**
 *
 * @author Darkasessine
 */
public class UdpReceiver extends Thread
{

    private DatagramSocket datagramSocket;
    private byte[] buffer;
    private PantaFrame frame;
    private JPanel panel;

    public UdpReceiver(PantaFrame frame)
    {
        this.frame = frame;
        panel = frame.getPanelScreen();

        try
        {
            datagramSocket = new DatagramSocket(4245);
            buffer = new byte[1024 * 100];
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
            ex.printStackTrace();
        }
    }

    private void getPackage() throws IOException
    {
        ArrayList<Byte> composedByteArray = new ArrayList<>();

        while (true)
        {
            try
            {

                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);

                

                String data = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                JSONObject message = new JSONObject(data);
                byte[] bytes = Base64.getDecoder().decode(message.getString("screen"));
                
                if (bytes.length != 0)
                {
                    frame.setIsSharing(false);
                }

                for (int i = 0; i < bytes.length; i++)
                {
                    composedByteArray.add(bytes[i]);
                }

                if (bytes.length == 0)
                {
                    byte[] image = new byte[composedByteArray.size()];

                    for (int i = 0; i < composedByteArray.size(); i++)
                    {
                        image[i] = composedByteArray.get(i);
                    }

                    BufferedImage frame = ImageIO.read(new ByteArrayInputStream(image));
                    panel.getGraphics().drawImage(frame, 0, 0, panel.getWidth(), panel.getHeight(), null);

                    composedByteArray.clear();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                composedByteArray.clear();
            }

        }
    }
}
