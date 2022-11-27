package KerberosEntities;

import Utilities.Comunicacion;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Scanner;

@SuppressWarnings("LanguageDetectionInspection")
public class Client {


    private final String id_cliente;
    private final InetAddress address_cliente;
    private String id_Servidor;
    private SealedObject ticket_tgs;
    private String password_cliente;
    private String id_TicketGrantingServer;
    private SealedObject ticket_servicio;
    private String clave_Cliente_TicketGrantingServer;
    private String clave_cliente_servidor;

    public Client(String id_cliente, InetAddress address_cliente) {
        this.id_cliente = id_cliente;
        this.address_cliente = address_cliente;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(
                "         -----------------------------------\n" +
                        "         --   IMPLEMENTACION KERBEROS 4   --\n" +
                        "         -------------- CLIENTE ------------\n" +
                        "         -----------------------------------\n");

        Scanner entrada = new Scanner(System.in);

        System.out.printf("\nIngresa la ip del cliente \t->");
        String ipCliente = entrada.nextLine();

        System.out.printf("\nIngresa la ip del Authentication Server \t->");
        String ipAS = entrada.nextLine();
        System.out.printf("\nIngresa el puerto del Authentication Server\t->");
        int puertoAS = Integer.parseInt(entrada.nextLine());

        System.out.printf("\nIngresa la ip del Ticket Granting Server \t->");
        String ipTGS = entrada.nextLine();
        System.out.printf("\nIngresa el puerto del Ticket Granting Server\t->");
        int puertoTGS = Integer.parseInt(entrada.nextLine());

        System.out.printf("\nIngresa la ip del Servidor \t->");
        String ipServiceServer = entrada.nextLine();
        System.out.printf("\nIngresa el puerto del Servidor\t->");
        int puertoServiceServer = Integer.parseInt(entrada.nextLine());
        System.out.println();


        Client cliente = new Client("1", InetAddress.getByName(ipCliente)); // crea un nuevo cliente con la ip indicada
        cliente.setId_TicketGrantingServer("1"); // se le asigna el id del TGS al cual se va a conectar


        /*
        Intercambio de servicio de autentificacion para obtener TGT
        ______________________________________________________________
        */

        System.out.println("\n" +
                "--------------------------------------------------\n" +
                "-(A) INTERCAMBIO DE SERVICIO DE AUNTENTIFICACION:-\n" +
                "-    PARA OBTENER TGT                            -\n" +
                "--------------------------------------------------");

        Socket conexionAS = Conexiones.obtenerConexion(puertoAS, ipAS); // se conecta con el Authentication Server
        InputStream inputStream = conexionAS.getInputStream();
        OutputStream outputStream = conexionAS.getOutputStream();

        cliente.realizarPeticionTGThaciaAS(outputStream);// Realiza el [paso 1] de la versión 4 Kerberos
        cliente.recibirTGTdesdeAS(inputStream);// Recibe el mensaje del AS del [paso 2] Kerberos

        conexionAS.close(); // cierra conexion con el Authentication Server

        /*
        Intercambio de TGS: para obtener un ticket que concede un Servicio
        ______________________________________________________________
        */

        System.out.println(
                "\n" +
                        "--------------------------------------------------\n" +
                        "-(B) INTERCAMBIO DE TGS: PARA OBTENER UN TICKET  -\n" +
                        "-    QUE CONCEDE UN SERVICIO                     -\n" +
                        "--------------------------------------------------");

        cliente.setId_Servidor("1"); // asigna id del servidor servicio


        Socket conexionTGS = Conexiones.obtenerConexion(puertoTGS, ipTGS); // conectarse con TGS
        inputStream = conexionTGS.getInputStream();
        outputStream = conexionTGS.getOutputStream();

        cliente.realizarPeticionTickethaciaTGS(outputStream);// Realiza el [paso 3] de la versión 4 Kerberos
        cliente.recibirTicketdesdeTGS(inputStream);// Recibe el mensaje del TGS del [paso 4] Kerberos

        conexionTGS.close(); // Cierra la conexion con el TGS

        /*
         Intercambio de autentificación cliente/servidor: para obtener un servicio
        ______________________________________________________________
        */

        System.out.println(
                "\n" +
                        "--------------------------------------------------\n" +
                        "-(C) INTERCAMBIO DE AUTENTIFICACION              -\n" +
                        "-    CLIENTE/SERVIDOR: PARA OBTENER UN SERVICIO  -\n" +
                        "--------------------------------------------------");

        Socket conexionServiceServer = Conexiones.obtenerConexion(puertoServiceServer, ipServiceServer); // conectarse con TGS
        inputStream = conexionServiceServer.getInputStream();
        outputStream = conexionServiceServer.getOutputStream();

        cliente.realizarPeticionServiciohaciaServidor(outputStream);// Realiza el [paso 5] de la versión 4 Kerberos
        cliente.recibirServiciodesdeServidor(inputStream); // // Recibe el servicio del [paso 6] desde el SErvidor

        conexionServiceServer.close();// cierra conexion con el Servidor
    }

    public void realizarPeticionTGThaciaAS(OutputStream outputStream) throws Exception {
        HashMap<String, Object> solicitudTGS = this.generarSolicitudTGS(); // crea el mensaje que se enviara al AS
        Comunicaciones.enviarObjeto(outputStream, solicitudTGS);//envia el mensaje al AS
    }

    public void recibirTGTdesdeAS(InputStream inputStream) throws Exception {

        SealedObject respuetaCifrada = (SealedObject) Comunicaciones.recibirObjeto(inputStream); // recibe la respuesta cifrada del AS
        HashMap<String, Object> respuestaDescifrada = (HashMap<String, Object>) AESUtils.desencriptarObjeto(respuetaCifrada, "ContraseniaCliente"); // desencripta la respuesta del as
        System.out.printf("Repuestas recibida desde el AS: %s \n\n", respuestaDescifrada);

        this.setClave_Cliente_TicketGrantingServer((String) respuestaDescifrada.get("[K-c_tgs]")); // obtiene la clave entre el cliente y el TGS
        this.setId_TicketGrantingServer((String) respuestaDescifrada.get("[Id-tgs]")); // obtiene el id del TGS
        this.setTicket_tgs((SealedObject) respuestaDescifrada.get("[Ticket-tgs]")); // obtiene el ticket-tgs cifrado
    }

    public void realizarPeticionTickethaciaTGS(OutputStream outputStream) throws Exception {
        HashMap<String, Object> solicitudIntercambioTGS = this.generarSolicitudIntercambioTGS(); // crea la solicitud de intercambio de Tickets hacia el TGS
        Comunicaciones.enviarObjeto(outputStream, solicitudIntercambioTGS); // envia la solicitud de Intercambio
    }

    public void recibirTicketdesdeTGS(InputStream inputStream) throws Exception {
        SealedObject respuestaCifrada = (SealedObject) Comunicaciones.recibirObjeto(inputStream); // recibe la respuesa cifrada desde el TGT
        HashMap<String, Object> respuesta =
                (HashMap<String, Object>) AESUtils.desencriptarObjeto(respuestaCifrada, this.getClave_Cliente_TicketGrantingServer()); // descifra el mensaje recibido con la clave entre el cliente y el TGS (K c-tgs)
        System.out.printf("Repuestas recibida: %s \n\n", respuesta);

        this.setTicket_servicio((SealedObject) respuesta.get("[Ticket-v]"));// obtiene el Ticket para el Servidor Cifrado
        this.setClave_cliente_servidor((String) respuesta.get("[K-c_v]"));// obtiene la clave entre el cliente y el servidor
    }

    public void realizarPeticionServiciohaciaServidor(OutputStream outputStream) throws Exception {
        HashMap<String, Object> peticionServicio = this.generarSolicitudIntercambioServicio(); // crea la solicitud de intercambio de servicio que se enviara al Servidor
        Comunicacion.enviarObjeto(outputStream, peticionServicio); // Envia la peticion de intercambio al Servidor
        System.out.printf("Peticion Intercambio Servicio Enviada: %s\n", peticionServicio);
    }

    public void recibirServiciodesdeServidor(InputStream inputStream) throws Exception {
        SealedObject respuestaCifrada = (SealedObject) Comunicacion.recibirObjeto(inputStream); // recibe la respuesta desde el servidor
        System.out.printf("respuesta cifrada recibida -> %s", respuestaCifrada);
        HashMap<String, Object> respuestaServicio = (HashMap<String, Object>) AESUtils.desencriptarObjeto(respuestaCifrada, this.getClave_cliente_servidor()); // descifra la respuesta con la clave entre el cliente y el servidor

        System.out.printf("\nrespuesta recibida Servicio: ", respuestaServicio);
        String servicioRecibido = (String) respuestaServicio.get("[Servicio]");

        System.out.println(servicioRecibido);
    }

    public String getClave_cliente_servidor() {
        return clave_cliente_servidor;
    }

    public void setClave_cliente_servidor(String clave_cliente_servidor) {
        this.clave_cliente_servidor = clave_cliente_servidor;
    }

    public SealedObject getTicket_servicio() {
        return ticket_servicio;
    }

    public void setTicket_servicio(SealedObject ticket_servicio) {
        this.ticket_servicio = ticket_servicio;
    }

    public String getClave_Cliente_TicketGrantingServer() {
        return clave_Cliente_TicketGrantingServer;
    }

    public void setClave_Cliente_TicketGrantingServer(String clave_Cliente_TicketGrantingServer) {
        this.clave_Cliente_TicketGrantingServer = clave_Cliente_TicketGrantingServer;
    }

    public void setPassword_cliente(String password_cliente) {
        this.password_cliente = password_cliente;
    }

    public void setId_TicketGrantingServer(String id_TicketGrantingServer) {
        this.id_TicketGrantingServer = id_TicketGrantingServer;
    }

    public HashMap<String, Object> generarSolicitudTGS() {

        HashMap<String, Object> solicitud = new HashMap<>();
        solicitud.put("[Id-c]", id_cliente);
        solicitud.put("[Id-tgs]", id_TicketGrantingServer);
        solicitud.put("[TimeStamp-1]", LocalDateTime.now());


        return solicitud;
    }

    public HashMap<String, Object> generarSolicitudIntercambioTGS() throws Exception {

        ClientAuthentication autentificador_cliente = new ClientAuthentication(id_cliente, address_cliente);

        HashMap<String, Object> solicitud = new HashMap<>();
        solicitud.put("[Id-v]", id_Servidor);
        solicitud.put("[Ticket-tgs]", ticket_tgs);

        SecretKey clave_cliente_TGS = (SecretKey) AESUtils.getKeyFromPassword(this.clave_Cliente_TicketGrantingServer);
        SealedObject autentificadorCifrado = AESUtils.encriptarObjeto(autentificador_cliente, clave_cliente_TGS);
        solicitud.put("[Autentificador-c]", autentificadorCifrado);

        return solicitud;
    }


    public HashMap<String, Object> generarSolicitudIntercambioServicio() throws Exception {

        ClientAuthentication autentificador_cliente = new ClientAuthentication(id_cliente, address_cliente);
        SecretKey clave_cliente_servidor = AESUtils.getKeyFromPassword(this.clave_cliente_servidor);
        SealedObject autentificadorCifrado = AESUtils.encriptarObjeto(autentificador_cliente, clave_cliente_servidor);

        HashMap<String, Object> solicitud = new HashMap<>();

        solicitud.put("[Ticket-v]", ticket_servicio); // envia el ticket -v para canjear por el servicio
        solicitud.put("[Autentificador-c]", autentificadorCifrado); // envia un nuevo autentificador cliente

        System.out.printf("\n[Ticket-v] cifrado y descifrado-> %s -> %s \n", ticket_servicio, AESUtils.desencriptarObjeto(ticket_servicio, "contraseñaServidor"));

        return solicitud;
    }

    public void setTicket_tgs(SealedObject ticket_tgs) {
        this.ticket_tgs = ticket_tgs;
    }

    public void setId_Servidor(String id_Servidor) {
        this.id_Servidor = id_Servidor;
    }

    public static class ClientAuthentication implements Serializable {

        private final String id_cliente;
        private final InetAddress address_cliente;
        private final LocalDateTime timeStamp_ClientAuthentication;

        public ClientAuthentication(String ID_cliente, String address_cliente) throws UnknownHostException {
            this.id_cliente = ID_cliente;
            this.address_cliente = InetAddress.getByName(address_cliente);
            timeStamp_ClientAuthentication = LocalDateTime.now();
        }

        public ClientAuthentication(String ID_cliente, InetAddress address_cliente) throws UnknownHostException {
            this.id_cliente = ID_cliente;
            this.address_cliente = address_cliente;
            timeStamp_ClientAuthentication = LocalDateTime.now();
        }

        public String getId_cliente() {
            return id_cliente;
        }

        public InetAddress getAddress_cliente() {
            return address_cliente;
        }

        public String getIp_cliente() {
            return address_cliente.getHostAddress();
        }

        public LocalDateTime getTimeStamp_ClientAuthentication() {
            return timeStamp_ClientAuthentication;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ClientAuthentication{");
            sb.append("id_cliente='").append(id_cliente).append('\'');
            sb.append(", address_cliente=").append(address_cliente);
            sb.append(", timeStamp_ClientAuthentication=").append(timeStamp_ClientAuthentication);
            sb.append('}');
            return sb.toString();
        }
    }

}
