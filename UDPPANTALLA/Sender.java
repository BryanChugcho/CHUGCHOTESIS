package UDPPANTALLA;

import static UDPPANTALLA.Sender.sendVideo;
//import com.sun.image.codec.jpeg.ImageFormatException;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
/**
 *
 * @author Darkasessine
 */

public class Sender {

    // Constantes
    public static int HEADER_SIZE = 8;
    public static int MAX_PACKETS = 255;
    public static int SESSION_START = 128;
    public static int SESSION_END = 64;
    public static int DATAGRAM_MAX_SIZE = 65507 - HEADER_SIZE;
    public static int MAX_SESSION_NUMBER = 255;
    public static String OUTPUT_FORMAT = "jpg";
    public static int COLOUR_OUTPUT = BufferedImage.TYPE_INT_RGB;
    public static double SCALING = 0.5;
    public static int SLEEP_MILLIS = 4000;
    public static String IP_ADDRESS = "224.0.0.1";
    public static int PORT = 8000;

    //Captura toda la pantalla y devuelve una imagen
    public static BufferedImage getScreenshot() throws AWTException, 
         IOException {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);

        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRect);

        return image;
    }

    // Convierte una imagen en una matriz de bytes
    public static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    //Cambia el tamaño de una imagen
    public static BufferedImage scale(BufferedImage source, int w, int h) {
        Image image = source
                .getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
        BufferedImage result = new BufferedImage(w, h, COLOUR_OUTPUT);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    //Disminuye la imagen
    public static BufferedImage shrink(BufferedImage source, double factor) {
        int w = (int) (source.getWidth() * factor);
        int h = (int) (source.getHeight() * factor);
        return scale(source, w, h);
    }
    
     Runnable task1;

    //Envia imagen por multicast
    private boolean sendImage(byte[] imageData, String multicastAddress, int port) {
        InetAddress ia;

        boolean ret = false;
        int ttl = 2;

        try {
            ia = InetAddress.getByName(multicastAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return ret;
        }

        MulticastSocket ms = null;

        try {
            ms = new MulticastSocket();
            ms.setTimeToLive(ttl);
            DatagramPacket dp = new DatagramPacket(imageData, imageData.length,ia, port);
            ms.send(dp);
            ret = true;
            System.out.println("envio mult");
        } catch (IOException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            if (ms != null) {
                ms.close();
            }
        }

        return ret;
    }

    public static void sendVideo() {

        Sender sender = new Sender();
        int sessionNumber = 0;
        boolean multicastImages = false;

        //Inicia envio
        try {
            //Tratando de enviar múltiples imágenes continuamente...
            while (true) {
                BufferedImage image;

                /* Toma la imagen */
                image = getScreenshot();

                /* Cambia el tamaño de la imagen */
                image = shrink(image, SCALING);
                byte[] imageByteArray = bufferedImageToByteArray(image, OUTPUT_FORMAT);
                int packets = (int) Math.ceil(imageByteArray.length / (float) DATAGRAM_MAX_SIZE);

              /* Si una imagen tiene más paquetes que el máximo genera un error */
                if (packets > MAX_PACKETS) {
                    System.out.println("image fuera de rango para envio!");
                    continue;
                }

                for (int i = 0; i <= packets; i++) {
                    int flags = 0;
                    flags = i == 0 ? flags | SESSION_START : flags;
                    flags = (i + 1) * DATAGRAM_MAX_SIZE > imageByteArray.length ? flags | SESSION_END : flags;

                    // Comprueba si el trozo de imagen tendrá el tamaño aceptable para el paquete
                    int size = (flags & SESSION_END) != SESSION_END ? DATAGRAM_MAX_SIZE : 
                            imageByteArray.length - i * DATAGRAM_MAX_SIZE;

                   /* Configura información adicional en la cabecera udp */
                    byte[] data = new byte[HEADER_SIZE + size];
                    data[0] = (byte) flags; //bandas de inicio y fin de sesión
                    data[1] = (byte) sessionNumber; //sesión a la que pertenece el paquete
                    data[2] = (byte) packets; //numero depaquets
                    data[3] = (byte) (DATAGRAM_MAX_SIZE >> 8);
                    data[4] = (byte) DATAGRAM_MAX_SIZE; // tamano de datagrama
                    data[5] = (byte) i; //qué trozo se envía, para arreglar al recibirlo
                    data[6] = (byte) (size >> 8);
                    data[7] = (byte) size;

                    /* copia el trozo actual en el array */
                    System.arraycopy(imageByteArray, i * DATAGRAM_MAX_SIZE, data, HEADER_SIZE, size);
                    /* envia multicast*/
                    sender.sendImage(data, IP_ADDRESS, PORT);

                    /* Sale del bucle cuando se envía el último paquete */
                    if ((flags & SESSION_END) == SESSION_END) {
                        break;
                    }
                }
                /* Tiempo de respiración para que la reproducción sea más pausada y lenta */
                Thread.sleep(SLEEP_MILLIS);

                /* Aumenta o número de sesiones */
                sessionNumber = sessionNumber < MAX_SESSION_NUMBER ? ++sessionNumber : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

       
      //  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     //  JLabel label = new JLabel();
      //  frame.getContentPane().add(label);
     ////   frame.setLocationRelativeTo(null);
      //  label.setText("Envio multicast captura de panmtalla ");
      // frame.setBounds(400,400,400,500);

        Thread t = new Thread(() -> {
             sendVideo();
        });
       
       t.start();

        
       

    }


}
