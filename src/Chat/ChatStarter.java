package Chat;

import java.net.Socket;

public class ChatStarter {
    Socket conexionEntrada;
    Socket conexionSalida;

    public ChatStarter(Socket conexionSalida, Socket conexionEntrada) {
        this.conexionSalida = conexionSalida;
        this.conexionEntrada = conexionEntrada;
    }

    public void iniciarChat(){

    }
}
