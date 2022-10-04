package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.TargetDataLine;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * @author Darkassesine
 */
public class UdpSender extends Thread {

    private static DatagramSocket datagramSocket;
    private static final int PORT = 4243;
    private byte[] buffer;
    private TargetDataLine targetDataLine;
    private boolean sendingAudio;

    public UdpSender(TargetDataLine targetDataLine) {
        this.targetDataLine = targetDataLine;
        this.sendingAudio = false;
        buffer = new byte[512];
        try {
            datagramSocket = new DatagramSocket();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setSendingAudio(boolean sendingAudio) {
        this.sendingAudio = sendingAudio;
    }

    public void sendRegistrationMessage() {
        JSONObject message = new JSONObject();
        try {
            message.put("audio", "");
        } catch (JSONException ex) {
            Logger.getLogger(UdpSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            try {
                message.put("ip", InetAddress.getLocalHost().getHostAddress());
            } catch (JSONException ex) {
                Logger.getLogger(UdpSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        DatagramPacket datagramPacket;
        try {
            datagramPacket = new DatagramPacket(message.toString().getBytes(),
                    message.toString().getBytes().length, InetAddress.getByName("192.168.100.178"), PORT);
            String test = datagramPacket.getAddress().toString();
            datagramSocket.send(datagramPacket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!sendingAudio) {
                    sleep(1);
                    continue;
                }

                targetDataLine.read(buffer, 0, buffer.length);
                byte[] base64ByteArray = Base64.getEncoder().encode(buffer);
                JSONObject message = new JSONObject();
                message.put("audio", new String(base64ByteArray));
                message.put("ip", InetAddress.getLocalHost().getHostAddress());

                DatagramPacket datagramPacket = new DatagramPacket(message.toString().getBytes(),
                        message.toString().getBytes().length, InetAddress.getByName("192.168.100.178"), PORT);
                datagramSocket.send(datagramPacket);
                sleep(1);

                //break;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
