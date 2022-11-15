package DistribucionClaves;

import Utilities.Comunicacion;
import Utilities.Conexiones;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;

import static Utilities.Comunicacion.recibirMensaje;

public class AutoridadCertificadora {
    private final int puertoEscuchaDistribucionClave = 5001;
    private ServerSocket socketDistribucionClaves;

    private KeyPairGenerator generador_claves_RSA;
    private Map<InetAddress, KeyPair> lista_ip_clave = new HashMap<>();


    public AutoridadCertificadora() throws Exception {
        generador_claves_RSA = KeyPairGenerator.getInstance("RSA");
    }

    public static void main(String[] args) throws Exception {
        AutoridadCertificadora AC = new AutoridadCertificadora();
        while (true) {
            AC.atenderPeticion();
        }
    }

    private Key obtenerClave(InetAddress ip_host, String tipo_clave) {
        KeyPair parClavesHost = obtenerParClaves(ip_host);
        Key claveSolicitada;

        switch (tipo_clave) {
            case "publica":
                claveSolicitada = parClavesHost.getPublic();
                break;
            case "privada":
                claveSolicitada = parClavesHost.getPrivate();
                break;
            default:
                claveSolicitada = null;
        }
        return claveSolicitada;
    }

    private KeyPair generarNuevoParDeClaves(InetAddress ip_host) {
        KeyPair nuevoParClaves = this.generador_claves_RSA.generateKeyPair();
        lista_ip_clave.put(ip_host, nuevoParClaves);
        return nuevoParClaves;
    }

    private KeyPair obtenerParClaves(InetAddress ip_host) {
//        Comprueba si ya hay claves asociadas con la ip, si no crea y le asocia un nuevo par de claves a la ip
        KeyPair parClaves = lista_ip_clave.containsKey(ip_host) ? lista_ip_clave.get(ip_host) : generarNuevoParDeClaves(ip_host);

        return parClaves;
    }

    public void atenderPeticion() throws IOException {

        System.out.println("\n--------------------------------------------------------------------------------\n" + "Esperando nueva conexion\n");
        socketDistribucionClaves = new ServerSocket(puertoEscuchaDistribucionClave);
        Socket socketConexion = Conexiones.aceptarConexionEntrante(puertoEscuchaDistribucionClave, this.socketDistribucionClaves);

        OutputStream outputStream = socketConexion.getOutputStream();
        InputStream inputStream = socketConexion.getInputStream();

        System.out.println("Conexion realizada con la IP: \t " + socketConexion.getInetAddress());
        Key clave_solicitada = procesar_solicitud_de_clave(outputStream, inputStream, socketConexion.getInetAddress());

        Comunicacion.enviarObjeto(outputStream, clave_solicitada);
        System.out.println("CLAVE ENVIADA: \n" + clave_solicitada);
        socketDistribucionClaves.close();
    }

    private Key procesar_solicitud_de_clave(OutputStream outputStream, InputStream inputStream, InetAddress inetAddress_solicitante) throws IOException {

        BufferedReader socketInputStreamReader = new BufferedReader(new InputStreamReader(inputStream));

        String tipo_de_peticion_clave = socketInputStreamReader.readLine();
        System.out.println("tipo Peticion " + tipo_de_peticion_clave);
        String ip_vinculada_a_clave_solicitada = socketInputStreamReader.readLine();
        System.out.println("ip solicitada " + ip_vinculada_a_clave_solicitada);

        String ip_solicitante = inetAddress_solicitante.getHostAddress();
        InetAddress ipSolicitada = InetAddress.getByName(ip_vinculada_a_clave_solicitada);

        String respuesta_peticion;
        Key clave_solicitada;
        switch (tipo_de_peticion_clave) {

            case "clave-privada":
                boolean puedeRecibirPrivada = ip_solicitante.equals(ip_vinculada_a_clave_solicitada);
                puedeRecibirPrivada = true;
                if (puedeRecibirPrivada) {
                    clave_solicitada = obtenerClave(ipSolicitada, "privada");
                    respuesta_peticion = "La clave privada fue encontrada y enviada";
                    System.out.format("La clave privada fue enviada \t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
                } else {
                    clave_solicitada = null;
                    respuesta_peticion = "Solo el dueño de la claves puede recibir la clave privada";
                    System.out.format("El solicitante no puede recibir la clave privada\t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
                }
                break;

            case "clave-publica":
                clave_solicitada = obtenerClave(ipSolicitada, "publica");
                respuesta_peticion = "La clave publica fue encontrada y enviada";
                System.out.format("La clave publica fue enviada \t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
                break;

            default:
                clave_solicitada = null;
                respuesta_peticion = "Tipo peticion no válida";
                System.out.format("El solicitante no realizo una petición invalida\t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
        }

        Comunicacion.enviarMensaje(respuesta_peticion, outputStream);

        return clave_solicitada;
    }

}