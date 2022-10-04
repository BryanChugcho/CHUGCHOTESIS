package CHAT;

/**
 *
 * @author Darkassesine
 */
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server {

    static ArrayList <Users> users = new ArrayList<>();
    static String[] nuevoUsuario;
    static String username;

    private static void sendMessage(String data, DatagramSocket socket) throws UnknownHostException, IOException {
        for (int i = 0; i < users.size(); i++) {
            NetworkTools.sendData(data, InetAddress.getByName(users.get(i).getIp()),
                    Integer.parseInt(users.get(i).getPort()), socket);
        }
    }

    public static void main(String args[]) throws IOException {
        DatagramSocket socket = null;

        //socket abre puertop
        socket = new DatagramSocket(4242);

        
        while (true) {
            String data = null, message;
            String s = NetworkTools.receiveData(socket);
            System.out.println("Servidor" + s + socket);
            //pd[0] = el texto del mensaje, pd[1] = IP comoString, pd[2]= Puerto @String
            String[] pd = s.split("#"); 
            //Eliminar el carácter "/" de la cadena  IP
            pd[1] = pd[1].replace("/", ""); 
            System.out.println("Servidor" + s );
            if (pd[0].contains("/Loggeo")) {
                // Separando el comando /loggeo y el nombre de usuario agregamos info al array
                nuevoUsuario = pd[0].split("/j/"); 
                users.add(new Users(nuevoUsuario[1], pd[1], pd[2]));
                System.out.println(users.toString()+"hola");
                //impresion consola nuevo miembro del grupo 
                System.out.println("Mensaje: " + pd[0] + " Desde ssss " + pd[1] + ":" + Integer.parseInt(pd[2]));
                message = "Nuevo usuario conectado -> " + nuevoUsuario[1];
                sendMessage(message, socket);
                //sentenecia else para poder ver quien se encuentra en le grupo 
            } else if (pd[0].equals("/grupo")) {
                ArrayList<String> onUsers = new ArrayList<>();
                for (int i = 0; i < users.size(); i++) {
                    onUsers.add(users.get(i).getUsername());
                }
                //impresion usuarios dentro del grupo a traves de la matriz 
                message = "Usuario en linea: " + onUsers;
                NetworkTools.sendData(message, InetAddress.getByName(pd[1]), Integer.parseInt(pd[2]), socket);
                System.out.println(message);
                System.out.println("prueba" + pd[2]);
                System.out.println(socket);
                //sentencia salir 
            } else if (pd[0].equals("/salir")) {
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getPort().equals(pd[2])) {
                        username = users.get(i).getUsername();
                        users.remove(i);
                    }
                }
                //impresion de persona que ha dejado el gru[p 
                message = username + " Ha dejado el chat";
                sendMessage(message, socket);
                System.out.println(message);
            } else {
                for (int i = 0; i < users.size(); i++) {
                    // ciclo de busqueda de la persona que se encargo de enviar el mensaje 
                    if (users.get(i).getPort().equals(pd[2]) && users.get(i).ip.equals(pd[1])) {
                        username = users.get(i).getUsername();
                    }
                    //impresion ed la informacion del mensaje que esta enviando el usuarioo logeado s
                    data = username + ": " + pd[0];
                    System.out.println(data + "que onda loca prueba");
                }
                //impresion del mensaje enviado junto con el usuario que lo envia 
                System.out.println("Envio: " + data);
                sendMessage(data, socket);
            }
        }
    }
}
