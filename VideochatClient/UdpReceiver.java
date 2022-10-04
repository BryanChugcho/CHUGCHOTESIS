/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VideoChatClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Darkassesine
 */
public class UdpReceiver extends Thread {

    private DatagramSocket datagramSocket;
    private byte[] buffer;
    private JPanel panelA;
    private JPanel panelB;
    private int idOwnCamera;
    private int idA;
    private int idB;

    public UdpReceiver(JPanel panelA, JPanel panelB, int idOwnCamera) {
        try {
            datagramSocket = new DatagramSocket(4244);
            buffer = new byte[1024 * 64];
            this.panelA = panelA;
            this.panelB = panelB;
            this.idOwnCamera = idOwnCamera;
        } catch (SocketException ex) {
            Logger.getLogger(UdpReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            recibirMensaje();
        } catch (IOException ex) {
            System.err.println("error en la lectura del mensaje");
        } catch (JSONException ex) {
            Logger.getLogger(UdpReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void recibirMensaje() throws IOException, JSONException {
        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);
            
            JSONObject message = new JSONObject( new String(datagramPacket.getData(), 0,
                    datagramPacket.getLength()));
            
            if (message.getInt("id") == idOwnCamera) {
                continue;
            }
            else if (message.getInt("id") == idA) {
                byte[] bytes = Base64.getDecoder().decode(message.getString("image"));
                BufferedImage frame = ImageIO.read(new ByteArrayInputStream(bytes));
                panelA.getGraphics().drawImage(frame, 0, 0, null);
            }
            else if (message.getInt("id") == idB) {
                byte[] bytes = Base64.getDecoder().decode(message.getString("image"));
                BufferedImage frame = ImageIO.read(new ByteArrayInputStream(bytes));
                panelB.getGraphics().drawImage(frame, 0, 0, null);
            }
            else if (idA == 0) {
                idA = message.getInt("id");
            }
            else if (idB == 0) {
                idB = message.getInt("id");
            }
        }
    }
}
