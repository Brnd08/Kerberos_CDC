package KerberosEntities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexiones {

    public static Socket obtenerConexion(int puertoDestino, String ipDestino) throws IOException {

        System.out.println("Conectando hacia: " + ipDestino + " en puerto: " + puertoDestino);

        InetAddress direccion_AC = InetAddress.getByName(ipDestino);
        Socket socket_hacia_AC = new Socket(direccion_AC, puertoDestino);

        System.out.println("Conexion realizada con la IP: " + direccion_AC + " en puerto: " + puertoDestino);
        return socket_hacia_AC;
    }

    public static Socket aceptarConexionEntrante(int puertoEscucha, ServerSocket serverSocket) throws IOException {

        System.out.println("Esperando Conexion en puerto : " + puertoEscucha);

        Socket socketConexion = serverSocket.accept();

        System.out.println("Conexion recibida con la IP: " + socketConexion.getInetAddress() + " en puerto: " + puertoEscucha);

        return socketConexion;
    }

}
