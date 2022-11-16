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
    //puerto servidor
    private final int puertoEscuchaDistribucionClave = 5001;
    //servidor
    private ServerSocket socketDistribucionClaves;
    //generador claves publicas y privadas
    private KeyPairGenerator generador_claves_RSA;
    //lista que relaciona ips con las claves
    private Map<InetAddress, KeyPair> lista_ip_clave = new HashMap<>();



    public AutoridadCertificadora() throws Exception {
        //inicializa el genrador de llaves publicas y privadas
        generador_claves_RSA = KeyPairGenerator.getInstance("RSA");
    }

    public static void main(String[] args) throws Exception {
        //crea objeto AutoridadCertificadora
        AutoridadCertificadora AC = new AutoridadCertificadora();
        //ejecuta indefinidamente el metodo atender peticion
        while (true) {
            AC.atenderPeticion();
        }
    }

    //Obtiene una clave (publica o privada) asociada a la ip
    private Key obtenerClave(InetAddress ip_host, String tipo_clave) {
        //obtiene el par de claves asociadas a la ip
        KeyPair parClavesHost = obtenerParClaves(ip_host);
        Key claveSolicitada;

        switch (tipo_clave) {
            //si la clave pedida es publica regresa la clave publica
            case "publica":
                claveSolicitada = parClavesHost.getPublic();
                break;
                // si la clave solicitada el privada regresa la privada
            case "privada":
                claveSolicitada = parClavesHost.getPrivate();
                break;
            default:
                claveSolicitada = null;
        }

        return claveSolicitada;
    }

    //genera nuevo par de claves (publicas y privadas) para la ip
    private KeyPair generarNuevoParDeClaves(InetAddress ip_host) {
        KeyPair nuevoParClaves = this.generador_claves_RSA.generateKeyPair();
        //guada la ip y claves generadas en la lista
        lista_ip_clave.put(ip_host, nuevoParClaves);
        return nuevoParClaves;
    }

    //Regresa un par de claves para la ip
    private KeyPair obtenerParClaves(InetAddress ip_host) {
//        Comprueba si ya hay claves asociadas con la ip, si no crea y le asocia un nuevo par de claves a la ip
        KeyPair parClaves = lista_ip_clave.containsKey(ip_host) ? lista_ip_clave.get(ip_host) : generarNuevoParDeClaves(ip_host);

        return parClaves;
    }

    //metodo que atiende la peticion del solicitante
    public void atenderPeticion() throws IOException {

        System.out.println("\n--------------------------------------------------------------------------------\n" + "Esperando nueva conexion\n");
        // asigna un nuevo socket
        socketDistribucionClaves = new ServerSocket(puertoEscuchaDistribucionClave);
        // acepta la conexion con el solicitante
        Socket socketConexion = Conexiones.aceptarConexionEntrante(puertoEscuchaDistribucionClave, this.socketDistribucionClaves);


        OutputStream outputStream = socketConexion.getOutputStream();
        InputStream inputStream = socketConexion.getInputStream();


        System.out.println("Conexion realizada con la IP: \t " + socketConexion.getInetAddress());
        // Obtiene la clave solicitada por el solicitante
        Key clave_solicitada = procesar_solicitud_de_clave(outputStream, inputStream, socketConexion.getInetAddress());

        //envia la clave al solicitante
        Comunicacion.enviarObjeto(outputStream, clave_solicitada);
        System.out.println("CLAVE ENVIADA: \n" + clave_solicitada);

        //cierra la conexion
        socketDistribucionClaves.close();
    }
    //metodo que  procesa la solicitud de clave
    private Key procesar_solicitud_de_clave(OutputStream outputStream, InputStream inputStream, InetAddress inetAddress_solicitante) throws IOException {

        BufferedReader socketInputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
        //recibe el tipo de clave solicitada (clave-publica o clave-privada)
        String tipo_de_peticion_clave = socketInputStreamReader.readLine();
        System.out.println("tipo Peticion " + tipo_de_peticion_clave);

        // recibe la ip solictidad
        String ip_vinculada_a_clave_solicitada = socketInputStreamReader.readLine();
        System.out.println("ip solicitada " + ip_vinculada_a_clave_solicitada);

        // obtiene la ip del solicitante
        String ip_solicitante = inetAddress_solicitante.getHostAddress();
        InetAddress ipSolicitada = InetAddress.getByName(ip_vinculada_a_clave_solicitada);

        String respuesta_peticion;
        Key clave_solicitada;

        switch (tipo_de_peticion_clave) {
            // en caso de solicitar clave privada
            case "clave-privada":
                // verifica que el solicitante sea el dueño de la clave privda
                boolean puedeRecibirPrivada = ip_solicitante.equals(ip_vinculada_a_clave_solicitada);
                puedeRecibirPrivada = true;

                if (puedeRecibirPrivada) { // si es el dueño de clave privada
                    // obtiene la clave solicitada
                    clave_solicitada = obtenerClave(ipSolicitada, "privada");
                    // crea mensaje de respuesta
                    respuesta_peticion = "La clave privada fue encontrada y enviada";
                    System.out.format("La clave privada fue enviada \t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
                } else { // si no es el dueño de la clave privada
                    // le da un valor nulo a la clave solicitada
                    clave_solicitada = null;
                    // indica en la respuesta que no puede recibir la clave privada de otra maquina
                    respuesta_peticion = "Solo el dueño de la claves puede recibir la clave privada";
                    System.out.format("El solicitante no puede recibir la clave privada\t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
                }
                break;
            // si se solicita clave publica
            case "clave-publica":
                // busca clave solicitada y la resguarda
                clave_solicitada = obtenerClave(ipSolicitada, "publica");
                // crea el mensaje de respuesta exitoso
                respuesta_peticion = "La clave publica fue encontrada y enviada";
                System.out.format("La clave publica fue enviada \t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
                break;
            // si no se indica que tipo de clave
            default:
                clave_solicitada = null;
                // indica que la peticion no es valida en el mensaje de respuesta
                respuesta_peticion = "Tipo peticion no válida";
                System.out.format("El solicitante no realizo una petición invalida\t IP-SOLICITANTE: %s | IP-SOLICITADA: %s \n", ip_solicitante, ipSolicitada);
        }
        // envia la respuesta al solicitante
        Comunicacion.enviarMensaje(respuesta_peticion, outputStream);

        return clave_solicitada;
    }

}