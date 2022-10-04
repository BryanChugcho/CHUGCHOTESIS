package CHAT;

import UDP.*;
import CHAT.NetworkTools;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 *
 * @author Darkassesine
 */
public class Clientesebas extends Thread {

    
    
        
    public Clientesebas() {
        int contador;
    }
    
    // mvariabeles globales
    static AudioInputStream auInputStream;
    static AudioFormat format;
    static int port = 50005;
    static float rate = 44100;

    static boolean status = true;
    static DataLine.Info dateLineInfo;
    static SourceDataLine sourceDataLine;

     
    public void run(){
        // TODO Auto-generated method stub
  while(true){
        System.out.println("Cliente Iniciado en el puerto :" + port);
        System.setProperty("java.net.preferIPv4Stack", "true");

     try{
            // creacion del grupo multicast
            // se utilizara la misma ip que en el envio
            InetAddress mcastaddt = InetAddress.getByName("225.6.7.8");
            // creacion del socket multicast utilizando el puerto declarado
            MulticastSocket multicastSocket = new MulticastSocket(port);

            InetSocketAddress group = new InetSocketAddress(mcastaddt, port);

            NetworkInterface net = NetworkInterface.getByName("bge0");

            // union al grupo multicast
            multicastSocket.joinGroup(group, net);
            
            multicastSocket.setReuseAddress(true);

            // data que recibiremos dentro del siguiente array de datos
            byte[] receiveData = new byte[4096];

            // conversion de paquetes de datos
            format = new AudioFormat(rate, 16, 2, true, false);

            // conversion de datos en sonido con el siguiento format de audio
            dateLineInfo = new DataLine.Info(SourceDataLine.class, format);

            // envio hacia los parlanets
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dateLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();

            // creacion del receptor de paquetes
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            // recibimos el paquete y podemos converit el paquete en datos
            ByteArrayInputStream baiss = new ByteArrayInputStream(receivePacket.getData());

            // loop de conversion
            while (status == true) {

                multicastSocket.receive(receivePacket);
                auInputStream = new AudioInputStream(baiss, format, receivePacket.getLength());

                toSpeaker(receivePacket.getData());
                

            }
            
           sourceDataLine.drain();
        sourceDataLine.close();
       
            

     } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    }
    

    
    
        
    
    
    // function cerado para enviar la informacion al hablante
    private static void toSpeaker(byte soundbytes[]) {
        // TODO Auto-generated method stub

        try {

            System.out.println("Al Hablante esto es lo que suena");
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
        } catch (Exception e) {
            // TODO: handle exception

            System.out.println("este problema" + e);
        }
    }

    public void recibeJlabel(javax.swing.JLabel jLabel1 ) {
        try {
            jLabel1.setText("Al hablante " + sourceDataLine );
            jLabel1.setText("Al Hablante esto es lo que suena");
        } catch (Exception e) {
            // TODO: handle exception

            System.out.println("este problema" + e);
        }

    }

    void receiveImage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static class sourceDataLine {

        public sourceDataLine() {
        }
    }
}
