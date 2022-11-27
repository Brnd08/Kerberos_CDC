package KerberosEntities;

import javax.crypto.SealedObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ServiceServer {

    public final String servicio =
            "\n                       .,,uod8B8bou,,.\n" +
                    "              ..,uod8BBBBBBBBBBBBBBBBRPFT?l!i:.\n" +
                    "         ,=m8BBBBBBBBBBBBBBBRPFT?!||||||||||||||\n" +
                    "         !...:!TVBBBRPFT||||||||||!!^^\"\"'   ||||\n" +
                    "         !.......:!?|||||!!^^\"\"'            ||||\n" +
                    "         !.........||||                     ||||\n" +
                    "         !.........||||  ##brdn             ||||\n" +
                    "         !.........||||  ##anet             ||||\n" +
                    "         !.........||||  ##luis             ||||\n" +
                    "         !.........||||  ##mau              ||||\n" +
                    "         !.........||||                     ||||\n" +
                    "         `.........||||                    ,||||\n" +
                    "          .;.......||||               _.-!!|||||\n" +
                    "   .,uodWBBBBb.....||||       _.-!!|||||||||!:'\n" +
                    "!YBBBBBBBBBBBBBBb..!|||:..-!!|||||||!iof68BBBBBb....\n" +
                    "!..YBBBBBBBBBBBBBBb!!||||||||!iof68BBBBBBRPFT?!::   `.\n" +
                    "!....YBBBBBBBBBBBBBBbaaitf68BBBBBBRPFT?!:::::::::     `.\n" +
                    "!......YBBBBBBBBBBBBBBBBBBBRPFT?!::::::;:!^\"`;:::       `.\n" +
                    "!........YBBBBBBBBBBRPFT?!::::::::::^''...::::::;         iBBbo.\n" +
                    "`..........YBRPFT?!::::::::::::::::::::::::;iof68bo.      WBBBBbo.\n" +
                    "  `..........:::::::::::::::::::::::;iof688888888888b.     `YBBBP^'\n" +
                    "    `........::::::::::::::::;iof688888888888888888888b.     `\n" +
                    "      `......:::::::::;iof688888888888888888888888888888b.\n" +
                    "        `....:::;iof688888888888888888888888888888888899fT!\n" +
                    "          `..::!8888888888888888888888888888888899fT|!^\"'\n" +
                    "            `' !!988888888888888888888888899fT|!^\"'\n" +
                    "                `!!8888888888888888899fT|!^\"'\n" +
                    "                  `!988888888899fT|!^\"'\n" +
                    "                    `!9899fT|!^\"'";
    private LocalDateTime timestamp5;
    private String clave_cliente_servidor;
    private String clave_servidor;
    private TicketGrantingServer.Ticket_servicio ticketServicio;
    private Client.ClientAuthentication clientAuthentication;

    public static void main(String[] args) throws Exception {
        System.out.println(
                "         -----------------------------------\n" +
                        "         --   IMPLEMENTACION KERBEROS 4   --\n" +
                        "         ------------- SERVIDOR ------------\n" +
                        "         -----------------------------------\n");

        System.out.println(
                "\n" +
                        "--------------------------------------------------\n" +
                        "-(C) INTERCAMBIO DE AUTENTIFICACION              -\n" +
                        "-    CLIENTE/SERVIDOR: PARA OBTENER UN SERVICIO  -\n" +
                        "--------------------------------------------------");

        ServiceServer serviceServer = new ServiceServer();
        serviceServer.setClave_servidor("contraseñaServidor"); // constraseña propia del servidor previamente distribuida la cual fue creada por el TGT

        int puertoServer = 2002;
        ServerSocket serverSocket = new ServerSocket(puertoServer);

        /*
         Intercambio de autentificación cliente/servidor: para obtener un servicio
        ______________________________________________________________
        */

        Socket conexionCliente = Conexiones.aceptarConexionEntrante(puertoServer, serverSocket);

        InputStream inputStream = conexionCliente.getInputStream();
        OutputStream outputStream = conexionCliente.getOutputStream();


        serviceServer.recibirPeticionServicioDesdeCliente(inputStream);// recibe la peticion de Intercambio de servicio desde el cliente [paso 5] Kerberos

        TicketGrantingServer.Ticket_servicio ticket_servicio = serviceServer.getTicketServicio(); // descifrado de la peticion
        Client.ClientAuthentication clientAuthentication = serviceServer.getClientAuthentication(); // Autentificacion del cliente descifrada
        boolean esClienteValido = serviceServer.validarClienteConTicket(ticket_servicio, clientAuthentication);// valida al cliente comparando sus datos con los que se indican en el Ticket Servicio
        System.out.printf("\n¿Coinciden los datos del cliente con los del ticket servidor? %s \n Datos cliente -> address: %s, id: %s ", esClienteValido ? "SI COINCIDEN" : "NO COINCIDEN", ticket_servicio.getAddress_cliente(), clientAuthentication.getId_cliente());
        if (!esClienteValido) // si los datos no coinciden termina la ejecucion
            return;

        serviceServer.responderPeticionServicioCliente(outputStream); // envia intercambio de servicion al cliente [paso 6] Kerberos
        conexionCliente.close(); // cierra la conexion con el Cliente

    }

    public Client.ClientAuthentication getClientAuthentication() {
        return clientAuthentication;
    }

    public void setClientAuthentication(Client.ClientAuthentication clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    public TicketGrantingServer.Ticket_servicio getTicketServicio() {
        return ticketServicio;
    }

    public void setTicketServicio(TicketGrantingServer.Ticket_servicio ticketServicio) {
        this.ticketServicio = ticketServicio;
    }

    public void recibirPeticionServicioDesdeCliente(InputStream inputStream) throws Exception {

        HashMap<String, Object> peticionServicio = (HashMap<String, Object>) Comunicaciones.recibirObjeto(inputStream); // recibe la peticion del servicio desde el cliente


        SealedObject ticketServicio_cifrado = (SealedObject) peticionServicio.get("[Ticket-v]"); // obtiene el ticket cifrado desde la peticion del cliente
        TicketGrantingServer.Ticket_servicio ticket_servicio =
                (TicketGrantingServer.Ticket_servicio) AESUtils.desencriptarObjeto(ticketServicio_cifrado, this.getClave_servidor()); // descifra el ticket con su clave servidor (K-v)
        this.setTicketServicio(ticket_servicio);// guarda el ticket-v descifrado
        this.setClave_cliente_servidor(ticket_servicio.getClave_cliente_servidor());
        ; // obtiene la contraseña entre el cliente y el servidor del ticket que se descifro


        SealedObject autentificadorCliente_cifrado = (SealedObject) peticionServicio.get("[Autentificador-c]"); // obtiene el autentificador cifrado del cliente
        Client.ClientAuthentication autentificadorCliente = (Client.ClientAuthentication) AESUtils.desencriptarObjeto(autentificadorCliente_cifrado, this.getClave_cliente_servidor()); // descifra el autentificador del cliente con la clave entre el cliente y el servidor (K_c-s)
        this.setClientAuthentication(autentificadorCliente);// guarda el Autentificador del Cliente Descifrado
        this.setTimestamp5(autentificadorCliente.getTimeStamp_ClientAuthentication()); // guarda el TimeStamp del autentificador del cliente

        System.out.printf("Peticion recibida desde el cliente: %s\n", peticionServicio);
        System.out.printf("TicketServicio descifrado : %s\n Autentificador del Cliente Descifrado\n", peticionServicio, autentificadorCliente);

    }

    public void responderPeticionServicioCliente(OutputStream outputStream) throws Exception {
        HashMap<String, Object> respuestaServicio = this.responderServicio(); // crea el mensaje de respuesta del servicio para el cliente
        SealedObject respuestaServicio_cifrada = AESUtils.encriptarObjeto(respuestaServicio, this.getClave_cliente_servidor()); // cifra la respuesta del servicio con la clave entre el cliente y el servidor (K_c-v)
        Comunicaciones.enviarObjeto(outputStream, respuestaServicio_cifrada); // envia de vuelta al cliente la respuesta cifrada
        System.out.printf("\nEl servicio ha sido otorgado al cliente");

        System.out.printf("Respuesta enviada: %s", respuestaServicio_cifrada);
    }

    public HashMap<String, Object> responderServicio() throws Exception {
        HashMap<String, Object> respuestaSolicitud = new HashMap<>();

        respuestaSolicitud.put("[TimeStamp-incrementada]", timestamp5.plusMinutes(1)); // añade el timeStamp del autentificador-cliente incrementado en un minuto

        respuestaSolicitud.put("[Servicio]", servicio); // añade el servicio al cliente


        return respuestaSolicitud;
    }

    private boolean validarClienteConTicket(TicketGrantingServer.Ticket_servicio ticket_servicio, Client.ClientAuthentication autentificadorCliente) {

        boolean esClienteValido;

        esClienteValido =
                (ticket_servicio.getId_cliente().equals(autentificadorCliente.getId_cliente())) // compara el id del ticket con el del autentificador
                        && (ticket_servicio.getIp_cliente().equals(autentificadorCliente.getIp_cliente()))// compara la ip especificada en el ticket con la ip del autentificador
                        && ticket_servicio.getTiempo_vida_ticket().isAfter(LocalDateTime.now());// Verifica si el tiempo de vida del ticket aun no ha pasado

        return esClienteValido;
    }

    public String getClave_servidor() {
        return clave_servidor;
    }

    public void setClave_servidor(String clave_servidor) {
        this.clave_servidor = clave_servidor;
    }

    public String getClave_cliente_servidor() {
        return clave_cliente_servidor;
    }

    public void setClave_cliente_servidor(String clave_cliente_servidor) {
        this.clave_cliente_servidor = clave_cliente_servidor;
    }

    public LocalDateTime getTimestamp5() {
        return timestamp5;
    }

    public void setTimestamp5(LocalDateTime timestamp5) {
        this.timestamp5 = timestamp5;
    }

}
