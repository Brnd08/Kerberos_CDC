package DistribucionClaves;

import Utilities.Adendum;
import Utilities.Comunicacion;
import Utilities.Conexiones;
import Utilities.RSACriptografo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Scanner;

public class ReceptorClaveSecreta extends SolicitanteClave {

    private int puertoEscuchaClave = 6000;
    private ServerSocket socketDistribucionClave = new ServerSocket(puertoEscuchaClave);

    public ReceptorClaveSecreta() throws IOException {
    }

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(System.in);

//       ReceptorClaveSecreta receptorClave = new ReceptorClaveSecreta();

//        System.out.println("Ingresa el puerto de la Autoridad Autentificadora");
//        int puerto_AC = Integer.parseInt(scan.nextLine());
        int puerto_AC = 5001;
//        System.out.println(puerto_AC);

        System.out.println("Ingresa la ip de la Autoridad Autentificadora");
        String ip_AC = scan.nextLine();
        System.out.println(ip_AC);


        System.out.println("Conectando con AC");

        Socket conexion_con_AC1 = Conexiones.obtenerConexion(puerto_AC, ip_AC);


//        String ip_propia = InetAddress.getLocalHost().getHostAddress();


        System.out.println("Ingresa la ip del emisor clave");
        String ip_emisor = scan.nextLine();
        System.out.println(ip_emisor);

        Key llave_publica_emisor = solicitar_clave(conexion_con_AC1, "clave-publica", ip_emisor);

        System.out.println("\nIngresa la ip del receptor clave");
        String ip_receptor = scan.nextLine();
        System.out.println(ip_receptor);

        Socket conexion_con_AC2 = Conexiones.obtenerConexion(puerto_AC, ip_AC);
        Key llave_privada_receptor = solicitar_clave(conexion_con_AC2, "clave-privada", ip_receptor);

        ReceptorClaveSecreta receptorClaveSecreta = new ReceptorClaveSecreta();

        System.out.println("conectando con emisor clave");

        Socket conexionEmisorClave = Conexiones.obtenerConexion(EmisorClaveSecreta.puertoEntrada, ip_emisor);

        InputStream inputStream = conexionEmisorClave.getInputStream();
        String claveSecretaCifrada = (String) Comunicacion.recibirObjeto(inputStream);
        System.out.println("clave secreta Cifrada" + claveSecretaCifrada);

        RSACriptografo RSACriptografo = new RSACriptografo("RSA");
        String claveSecreta = RSACriptografo.descifrarString(claveSecretaCifrada, llave_publica_emisor);
        System.out.println("clave secreta" + claveSecreta);

        byte adendum_clave_secreta = Adendum.obtenerAdendumClave(claveSecreta);
        System.out.println("Adendum clave recibida " + adendum_clave_secreta);

        String adendumCifrado = (String) Comunicacion.recibirObjeto(inputStream);
        byte adendumDescifrado = Adendum.descifrarAdendum(adendumCifrado, llave_privada_receptor);

        System.out.println("Adendum descifrado " + adendumDescifrado);
    }
}
