package VideoChatClient;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.json.JSONObject;
/**
 *
 * @author Darkassesine
 */
public class UdpSender extends Thread {

    private static DatagramSocket datagramSocket;
    private static InetAddress direcciwonServidor;
    private static final int PUERTO = 4244;
    public static Webcam webcam = null;
    private JPanel panelOwnCamera;
    private int idOwnCamera;

    public UdpSender(JPanel panelOwnCamera, int idOwnCamera) {
        try {
            datagramSocket = new DatagramSocket();
            this.panelOwnCamera = panelOwnCamera;
            this.idOwnCamera = idOwnCamera;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        while (true) {
            BufferedImage frame = webcam.getImage();

            panelOwnCamera.getGraphics().drawImage(frame, 0, 0, null);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(frame, "jpeg", byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();

                byte[] base64ByteArray = Base64.getEncoder().encode(bytes);
                JSONObject message = new JSONObject();
                message.put("id", idOwnCamera);
                message.put("image", new String(base64ByteArray));
                DatagramPacket paquete = new DatagramPacket(message.toString().getBytes(),
                        message.toString().getBytes().length,
                        InetAddress.getByName("192.168.100.178"), PUERTO);
                datagramSocket.send(paquete);
                sleep(200);
            } catch (Exception ex) {
                System.out.println("Error al enviar la imagen");
                webcam.close();
            }
            //break;
        }
    }
}
