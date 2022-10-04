package CHAT;

import UDP.*;
import VideoChatClient.ClienteUDP;
import VideoChatClient.ChatFrame;
import VideoChatClient.VideochatClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Toggle;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import org.json.JSONException;
import pantallaClient.PantaFrame;



 /**
 *
 * @author Darkassesine
 */

public class Cliente {

    JFrame frame = new JFrame("UDP Cliente ");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    FlowLayout decision = new FlowLayout(FlowLayout.CENTER);
    JToggleButton encen = new JToggleButton("Microfono Encender" , false);
   // JButton mic_Apa = new JButton("Microfono Apagar");
    JButton compPan = new JButton("compartir Pantalla");
    JButton ver = new JButton("Video");
    JToggleButton Nover = new JToggleButton("Video Encender", false );
    String username = null, s, data;
    String[] sp;
    InetAddress serverAddress = null;
    int serverPort = 4242;
    DatagramSocket socket = null;
    Clientesebas h;
    VideochatClient sw;
    ClienteUDP uds;
    ChatFrame cf;
    PantaFrame pf;

   
    private String getServerAddress() throws UnknownHostException {
        String serverIP = null;
        do {
            serverIP = (String) JOptionPane.showInputDialog(
                    frame,
                    "Ingresar IP del servidor:",
                    "Bienvenido al chat",
                    JOptionPane.QUESTION_MESSAGE, null, null, InetAddress.getLocalHost().getHostAddress());
        } while ((serverIP == null || (serverIP != null && ("".equals(serverIP))))); 
        return serverIP;
    }


    private String getName() {
        String name = null;
        do {
            name = JOptionPane.showInputDialog(
                    frame,
                    "Nombre de Usuario:",
                    "Ingrese su nombre de Usuario",
                    JOptionPane.PLAIN_MESSAGE);
            // No se procesa si es nulo o el usuario pulsa el botón de Cancelar
        } while ((name == null || (name != null && ("".equals(name)))));
        return name;
    }

    
    
    
    
    private void run() throws IOException {
        //Convertir la IP del destinatario de String a InternetAddress
        String serverIP = getServerAddress();

        try {
            serverAddress = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Crea un socker para enviar y recibir
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
//Manejar el inicio de sesión Envío /iniciar sesión--hysername, lo divido como (--)
//al servidor para ir al if de /signin y obtener el hysername del usuario
        String newUser = "/Loggeo";
        username = getName();
        newUser = newUser.concat("/j/" + username);
        frame.setTitle("UDP Cliente /Servidor mensaje | " + username);
        /*System.out.println(newUser + "hola"); */
        NetworkTools.sendData(newUser, serverAddress, serverPort, socket);
        s = NetworkTools.receiveData(socket);

        sp = s.split("#");//sp0] = texto del mensaje, sp1] = IP del remitente como cadena Ps[2]= Puerto del remitente como cadena
        System.out.println("Servidor: " + sp[0] + sp[2]);
        messageArea.append("Para enviar mensajes solo escribir y presionar enter" + "\n");
        messageArea.append("Comandos de control : /grupo, /salir" + "\n");
        System.out.println(s + "cada conexion de un nuevo usuario");
        messageArea.append("Servidor: " + sp[0] + "\n");
        textField.setEditable(true);

        while (true) {
            data = null;
            try {
                String s = NetworkTools.receiveData(socket);
                //sp[0] = mensaje, sp[1] = IP del remitente, sp[2]= puerto del remitente
                // la IP y el puerto son los del servidor que recibe y reenvía el mensaje
                String[] sp = s.split("#");
                System.out.println("Mensaje: " + sp[0] + " Desde " + sp[1] + ":" + Integer.parseInt(sp[2]));
                System.out.println("puerto sp2" + sp[2]);
                messageArea.append(sp[0] + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Cliente() {
        // Interface de usuario
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.getContentPane().setLayout(decision);
        frame.getContentPane().add(compPan);
        frame.getContentPane().add(Nover);
        frame.getContentPane().add(encen);
      //  frame.getContentPane().add(mic_Apa);
        frame.setBounds(500, 300, 500, 300);
        
        
        //Variables de Audio
        UdpSender sender;
        Audio audio = new Audio();
        UdpReceiver receiver = new UdpReceiver (audio.getSourceDataLine());
        receiver.start();
        
        sender = new UdpSender(audio.getTargetDataLine());
        sender.sendRegistrationMessage();
        sender.start();

      
       
        // Add Listeners
        textField.addActionListener((ActionEvent e) -> {
            try {
                NetworkTools.sendData(textField.getText(), serverAddress, serverPort, socket);
                System.out.println("esto actionlistener" + serverPort);
                if (textField.getText().equals("/salir")) {

                    System.exit(0);
                }
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            textField.setText("");
        }
        );

          /*  Thread t = new Thread(() -> {
             sendVideo();
             
        });
               */

         
       //add listener toggle encender/apagar video frameo
        compPan.addActionListener((ActionEvent e) -> {
            {
                try {
                    /*
                    if(compPan.isSelected()){
                    try {
                    //Om=n state
                    pf = new PantaFrame();
                    } catch (JSONException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    compPan.setText("Video Apagar");
                    
                    }else {
                    pf.dispose();
                    
                    compPan.setText("Video Encender");
                    
                    
                    }
                    
                    */
                    
                    pf = new PantaFrame();
                } catch (JSONException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                  pf.setVisible(true); 
                
            }
        });

     
        //add listener toggle encender/apagar video frameo
        Nover.addActionListener((ActionEvent e) -> {
            {
                
                if(Nover.isSelected()){
                 //Om=n state
                 
                 cf.setVisible(true); 
                 Nover.setText("Video Apagar");
                 
                 }else {
                cf.setVisible(false);
                Nover.setText("Video Encender");
               
                 
                }
                        

                
            }
        });
/// toggle button activar/desactivar microfonoo
            encen.addActionListener((ActionEvent e) -> {
            {
                if(encen.isSelected()){
                 //Om=n state
                 sender.setSendingAudio(true);
                 encen.setText("Microfono Apagar");
                 }else {
                
                  sender.setSendingAudio(false);
                  encen.setText("Microfono Encender");
                 
                }
                        
// setDefaultCloseOperation(cf.EXIT_ON_CLOSE);
                
            }
        });
        
        
        
        
        
        
        
        
        //-----------------------------------------------------------//
        
        
        
    }

    public static void main(String[] args) throws IOException {
        Cliente cliente = new Cliente();
        cliente.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cliente.frame.setVisible(true);
        //cliente.textField.setBackground(Color.WHITE);
        cliente.frame.setResizable(false);
        cliente.frame.setLocationRelativeTo(null);
        cliente.run();
    }
}
