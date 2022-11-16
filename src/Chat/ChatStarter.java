package Chat;

import Utilities.Comunicacion;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatStarter {
    Socket conexionEntrada;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    private AESCriptografo criptografo;

    public ChatStarter( Socket conexionEntrada, String claveSecreta) throws Exception {
        this.conexionEntrada = conexionEntrada;
        this.printWriter = new PrintWriter(conexionEntrada.getOutputStream());
        this.bufferedReader = new BufferedReader(new InputStreamReader(conexionEntrada.getInputStream()));
        this.criptografo  = new AESCriptografo(claveSecreta);
    }

    public void iniciarChat() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("----------------CHAT INICIALIZADO----------------");


        while(true){
            String mensaje = scanner.nextLine();
            System.out.println(mensaje);
            String mensajeCifrado = this.criptografo.encriptarMensaje(mensaje);
            Comunicacion.enviarMensaje(mensajeCifrado, this.printWriter);//envia mensaje

            String mensajeCifradoRecibido = Comunicacion.recibirMensaje(this.bufferedReader);//espera un nuevo mensaje
            System.out.println("cifrado Recibido " + mensajeCifradoRecibido);

            String mensajeDescifrado = this.criptografo.desencriptarMensaje(mensajeCifradoRecibido);
            System.out.println(mensajeDescifrado);

        }
    }

    public void saludar(){
        Comunicacion.enviarMensaje("Hola desde la otra maquina", this.printWriter);
    }
}
