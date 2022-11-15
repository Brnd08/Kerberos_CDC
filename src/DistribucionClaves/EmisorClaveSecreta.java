package DistribucionClaves;

import Utilities.Comunicacion;
import Utilities.Conexiones;
import Utilities.RSACriptografo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Scanner;

import static Utilities.Adendum.cifrarAdendum;
import static Utilities.Adendum.obtenerAdendumClave;

public class EmisorClaveSecreta extends SolicitanteClave{
    static int puertoEntrada = 4000;

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(System.in);

        EmisorClaveSecreta emisorClave = new EmisorClaveSecreta();

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

        Key llave_privada_emisor = solicitar_clave(conexion_con_AC1, "clave-privada", ip_emisor);

        System.out.println("\nIngresa la ip del receptor clave");
        String ip_receptor = scan.nextLine();
        System.out.println(ip_receptor);

        Socket conexion_con_AC2 = Conexiones.obtenerConexion(puerto_AC, ip_AC);
        Key llave_publica_receptor = solicitar_clave(conexion_con_AC2, "clave-publica", ip_receptor);


        System.out.println("Ingresa la clave Secreta");

        String claveSecreta = scan.nextLine();
        System.out.println(claveSecreta);

        RSACriptografo RSACriptografo = new RSACriptografo("RSA");
        String claveSecretaCifrada = RSACriptografo.cifrarString(claveSecreta, llave_privada_emisor);
        System.out.println("clave Secreta Cifrada " + claveSecretaCifrada);




        byte adendum_clave_secreta = (obtenerAdendumClave(claveSecreta));
        System.out.println( "\nadendum clave " + adendum_clave_secreta);
        String adendum_cifrado = cifrarAdendum(adendum_clave_secreta, llave_publica_receptor);
        System.out.println("adendum clave cifrado " + adendum_cifrado);


//        System.out.println("ingresa puerto receptor: ");
//        int puertoReceptor = Integer.parseInt(scan.nextLine());

        System.out.println("Esperando conexion con receptor");

        ServerSocket socketDistribucionClaves = new ServerSocket(puertoEntrada);
        Socket conexion_receptor = Conexiones.aceptarConexionEntrante(puertoEntrada, socketDistribucionClaves);

        OutputStream outputStreamReceptor = conexion_receptor.getOutputStream();
        InputStream inputStreamReceptor = conexion_receptor.getInputStream();

        Comunicacion.enviarObjeto(outputStreamReceptor, claveSecretaCifrada);
        System.out.println("Clave Secreta Enviada");

        Comunicacion.enviarObjeto(outputStreamReceptor, adendum_cifrado);
        System.out.println("Adendum Secreto Enviado");


    }
}
