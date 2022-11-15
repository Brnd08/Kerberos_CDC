package DistribucionClaves;

import Utilities.Conexiones;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.Scanner;

import static Utilities.Comunicacion.*;
import static Utilities.Comunicacion.recibirMensaje;

public class SolicitanteClave {
    public static Key solicitar_clave(Socket socket_hacia_AC, String tipoClave, String ipAsociadaClave) throws Exception {

        InputStream inputStream = socket_hacia_AC.getInputStream();
        OutputStream outputStream = socket_hacia_AC.getOutputStream();

        enviarMensaje(tipoClave, outputStream);
        enviarMensaje(ipAsociadaClave, outputStream);

        System.out.println("Esperando respuesta Autoridad Certificadora");

        String respuestaPeticion = recibirMensaje(inputStream);

        System.out.println("Solicitando " + tipoClave + " de la maquina " + ipAsociadaClave);
        System.out.println("Respuesta recibida: " + respuestaPeticion);

        Key llaveRecibida = (Key) recibirObjeto(inputStream);
        System.out.println("Llave recibida: \n" + llaveRecibida);

        socket_hacia_AC.close();

        return llaveRecibida;
    }
}
