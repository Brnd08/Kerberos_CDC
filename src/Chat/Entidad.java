package Chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Entidad {

    int puertoReceptorChat;
    private ServerSocket serverSocket;

    private Socket socketEntrada;

    private Socket socketSalida;


    public Entidad(int puerto) throws IOException {
        this.puertoReceptorChat = puerto;
        serverSocket = new ServerSocket(); //crea el serverSocket
    }

    public int getPuertoReceptor() {
        return puertoReceptorChat;
    }

    public void setPuertoReceptor(int puertoReceptor) {
        this.puertoReceptorChat = puertoReceptor;
    }
}
