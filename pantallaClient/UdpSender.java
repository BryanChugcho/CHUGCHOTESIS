package pantallaClient;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JPanel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Darkasessine
 */
public class UdpSender extends Thread {

    private static final int PACKAGE_SIZE = 1024 * 62;
    private static DatagramSocket datagramSocket;
    private static final int PORT = 4245;
    private byte[] buffer;
    private Robot robot;
    private Rectangle area;

    private JPanel panel;
    private boolean isSharing;

    public UdpSender(JPanel jPanel) {
        panel = jPanel;

        isSharing = false;

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            ex.printStackTrace();
        }

        area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        try {
            datagramSocket = new DatagramSocket();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void sendRegistrationMessage() throws JSONException {
        JSONObject message = new JSONObject();
        message.put("screen", "");

        DatagramPacket datagramPacket;
        try {

            datagramPacket = new DatagramPacket(message.toString().getBytes(), message.toString().getBytes().length, InetAddress.getByName("192.168.100.178"), PORT);
            datagramSocket.send(datagramPacket);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setIsSharing(boolean isSharing) {
        this.isSharing = isSharing;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!isSharing) {
                    sleep(1);
                    continue;
                }

                BufferedImage bufferedImage = robot.createScreenCapture(area);

                panel.getGraphics().drawImage(bufferedImage, 0, 0, null);

                /*byteArrayOutputStream = new ByteArrayOutputStream();
                //ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = (ImageWriter) writers.next();

                ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
                writer.setOutput(ios);

                //bufferedImage = compress(1, bufferedImage);
                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(1f);
                writer.write(null, new IIOImage(bufferedImage, null, null), param);*/
                //g.getClip().
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

                byte[] bytes = byteArrayOutputStream.toByteArray();

                byte[] base64ByteArray = Base64.getEncoder().encode(bytes);

                int partsCount = (int) Math.ceil(base64ByteArray.length / (double) PACKAGE_SIZE);

                for (int i = 0; i < partsCount; i++) {
                    if (i == partsCount - 1) {
                        buffer = Arrays.copyOfRange(base64ByteArray, i * PACKAGE_SIZE, base64ByteArray.length);
                    } else {
                        buffer = Arrays.copyOfRange(base64ByteArray, i * PACKAGE_SIZE, PACKAGE_SIZE * (i + 1));
                    }

                    JSONObject message = new JSONObject();
                    message.put("screen", new String(buffer));

                    DatagramPacket paquete = new DatagramPacket(message.toString().getBytes(), message.toString().getBytes().length, InetAddress.getByName("192.168.100.178"), PORT);
                    datagramSocket.send(paquete);

                    if (i == partsCount - 1) {
                        message.put("screen", "");
                        paquete = new DatagramPacket(message.toString().getBytes(), message.toString().getBytes().length, InetAddress.getByName("192.168.100.178"), PORT);
                        datagramSocket.send(paquete);
                    }

                }

                sleep(10);
                //break;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private BufferedImage compress(int compression, BufferedImage bi) throws FileNotFoundException, IOException {
        Iterator<ImageWriter> i = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter jpegWriter = i.next();

        // Set the compression quality
        ImageWriteParam param = jpegWriter.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.1f * compression);

        BufferedImage byteArrayOutputStream = new BufferedImage(1920, 1080, BufferedImage.SCALE_FAST);
        // Write the image to a file
        FileImageOutputStream out = new FileImageOutputStream(new File("Lenna" + compression + ".jpg"));
        jpegWriter.setOutput(byteArrayOutputStream);
        jpegWriter.write(null, new IIOImage(bi, null, null), param);
        jpegWriter.dispose();
        out.close();

        return byteArrayOutputStream;
    }
}
