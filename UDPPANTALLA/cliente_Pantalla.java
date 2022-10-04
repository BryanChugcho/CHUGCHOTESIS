package UDPPANTALLA;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 *
 * @author Darkasessine
 */
public class cliente_Pantalla implements KeyListener {

    // Constantes
    public static int HEADER_SIZE = 8;
    public static int SESSION_START = 128;
    public static int SESSION_END = 64;

    /*
    El tamaño máximo de un paquete datagrama es 65507, el tamaño máximo de un paquete ip es 
    65535 menos 20 bytes para la cabecera IP y 8 bytes para la cabecera UDP
     */
    private static int DATAGRAM_MAX_SIZE = 65507;

    /* Ajustes y valores para trabajar con el envío */
    public static String IP_ADDRESS = "224.0.0.1";// dirección de multidifusión para crear el grupo y tener comunicación
    //Configuración de la pantalla
    JFrame frame;
    boolean fullscreen = false;
    JWindow fullscreenWindow = null;

    // Coordenadas o evento de teclado
    public void keyPressed(KeyEvent keyevent) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        /* Cambia a pantalla completa cuando se pulsa */
        if (fullscreen) {
            device.setFullScreenWindow(null);
            fullscreenWindow.setVisible(false);
            fullscreen = false;
        } else {
            device.setFullScreenWindow(fullscreenWindow);
            fullscreenWindow.setVisible(true);
            fullscreen = true;
        }

    }

    public void keyReleased(KeyEvent keyevent) {
    }

    public void keyTyped(KeyEvent keyevent) {
    }

    // Método principal para recibir imágenes
    private void receiveImages(String multicastAddress, int port) {

        boolean debug = true;
        InetAddress ia = null;
        MulticastSocket ms = null;

        /* Construccion del Frame*/
        JLabel labelImg = new JLabel();
        JLabel windowImg = new JLabel();
        JButton btnAudio = new JButton();

        frame = new JFrame("Cliente Chat tesis audio/imagen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(labelImg);

        frame.setSize(300, 10);
        frame.setVisible(true);
        //evento de tecla
        frame.addKeyListener(this);

        /* Construye la ventana en modo de pantalla completa */
        fullscreenWindow = new JWindow();
        fullscreenWindow.getContentPane().add(windowImg);
        fullscreenWindow.addKeyListener(this);

        try {
            /* Pega IP */
            ia = InetAddress.getByName(multicastAddress);

            /* Configura el socket y se une al grupo */
            ms = new MulticastSocket(port);
            ms.joinGroup(ia);

            int currentSession = -1; //la sesión se inicia en negativo para el control
            int slicesStored = 0;
            int[] slicesCol = null;
            byte[] imageData = null;
            boolean sessionAvailable = false;

            /* Matriz de bytes para almacenar lo que se recibe */
            byte[] buffer = new byte[DATAGRAM_MAX_SIZE];

            while (true) {
                /* Recibe paquete UDP */
                DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                ms.receive(dp);
                byte[] data = dp.getData();

                /* Lee la cabecera para organizar */
                short session = (short) (data[1] & 0xff);
                short slices = (short) (data[2] & 0xff);
                int maxPacketSize = (int) ((data[3] & 0xff) << 8 | (data[4] & 0xff));

                short slice = (short) (data[5] & 0xff);
                int size = (int) ((data[6] & 0xff) << 8 | (data[7] & 0xff));

                /* SESSION_START True, establece los valores iniciales */
                if ((data[0] & SESSION_START) == SESSION_START) {
                    if (session != currentSession) {
                        currentSession = session;
                        slicesStored = 0;
                        /* Construye una matriz de bytes de tamaño apropiado */
                        imageData = new byte[slices * maxPacketSize];
                        slicesCol = new int[slices];
                        sessionAvailable = true;
                    }
                }

                /* Si el paquete pertenece a la sesión actual, captura el trozo de la imagen
                y comprueba el envío al array*/
                if (sessionAvailable && session == currentSession) {
                    if (slicesCol != null && slicesCol[slice] == 0) {
                        slicesCol[slice] = 1;
                        System.arraycopy(data, HEADER_SIZE, imageData, slice
                                * maxPacketSize, size);
                        slicesStored++;
                    }
                }

                /* Si la imagen está completa, la muestra*/
                if (slicesStored == slices) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(bis);
                    labelImg.setIcon(new ImageIcon(image));
                    windowImg.setIcon(new ImageIcon(image));

                    frame.pack();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ms != null) {
                try {
                    ms.leaveGroup(ia);
                    ms.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    
    public static void main(String[] args) {

        Runnable task1 = () -> {
            cliente_Pantalla cliente_Pantalla = new cliente_Pantalla();
            cliente_Pantalla.receiveImages(IP_ADDRESS, 8000);
        };


        new Thread(task1).start();
       
    }

    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
