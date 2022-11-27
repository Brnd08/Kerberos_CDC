package KerberosEntities;

import javax.crypto.SealedObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;

import static KerberosEntities.AESUtils.encriptarObjeto;

public class TicketGrantingServer {
    //    private Key clave_cliente_ticketGrantingServer;
//    private Key clave_cliente_servidor;
//    private Key clave_serviceServer;
    private String id_servidor;
    private String id_cliente;
    private InetAddress address_cliente;
    private Client.ClientAuthentication autificacionCliente;
    private AuthenticationServer.Ticket_TGS ticket_tgs;

    public static void main(String[] args) throws Exception {
        System.out.println(
                "         -----------------------------------\n" +
                        "         --   IMPLEMENTACION KERBEROS 4   --\n" +
                        "         --------------   TGT  -------------\n" +
                        "         -----------------------------------\n");


        System.out.println(
                "\n" +
                        "--------------------------------------------------\n" +
                        "-(B) INTERCAMBIO DE TGS: PARA OBTENER UN TICKET  -\n" +
                        "-    QUE CONCEDE UN SERVICIO                     -\n" +
                        "--------------------------------------------------");

        TicketGrantingServer TGS = new TicketGrantingServer();

        int puertoServer = 2001;
        ServerSocket serverSocket = new ServerSocket(puertoServer);

        /*
        Intercambio de TGS: para obtener un ticket que concede un Servicio
        ______________________________________________________________
        */

        Socket conexionCliente = Conexiones.aceptarConexionEntrante(puertoServer, serverSocket);
        InputStream inputStream = conexionCliente.getInputStream();
        OutputStream outputStream = conexionCliente.getOutputStream();

        TGS.recibirPeticionTicketDesdeCliente(inputStream); // recibe el mensaje desde el cliente del [paso 3] Kerberos

        Client.ClientAuthentication autenticacionCliente = TGS.getAutificacionCliente();// Autentificacion del cliente de la peticion
        AuthenticationServer.Ticket_TGS ticket_tgs = TGS.getTicket_tgs();// Ticket TGS de la peticion
        boolean coinciden = // verifica que los datos del ticket coincidan con los datos del ticket
                autenticacionCliente.getIp_cliente().equals(ticket_tgs.getIp_cliente()) && autenticacionCliente.getId_cliente().equals(ticket_tgs.getId_cliente());
        System.out.printf("\n¿Coinciden los datos del cliente con los del ticket TGS? %s \n Datos cliente -> address: %s, id: %s ", coinciden ? "SI COINCIDEN" : "NO COINCIDEN", autenticacionCliente.getAddress_cliente(), autenticacionCliente.getId_cliente());

        TGS.enviarRespuestaTicketAlCliente(outputStream);// envia el mensaje de respuesta al cliente [paso 4] Kerberos

        conexionCliente.close();// cierra la conexion con el cliente
    }

    public Client.ClientAuthentication getAutificacionCliente() {
        return autificacionCliente;
    }

    public void setAutificacionCliente(Client.ClientAuthentication autificacionCliente) {
        this.autificacionCliente = autificacionCliente;
    }

    public AuthenticationServer.Ticket_TGS getTicket_tgs() {
        return ticket_tgs;
    }

    public void setTicket_tgs(AuthenticationServer.Ticket_TGS ticket_tgs) {
        this.ticket_tgs = ticket_tgs;
    }

    public void recibirPeticionTicketDesdeCliente(InputStream inputStream) throws Exception {

        HashMap<String, Object> peticion = (HashMap<String, Object>) Comunicaciones.recibirObjeto(inputStream); // recibe la peticion desde el cliente
        System.out.printf("Solicitud recibida: %s \n\n", peticion);

        Client.ClientAuthentication autenticacionCliente = (Client.ClientAuthentication)
                AESUtils.desencriptarObjeto((SealedObject) peticion.get("[Autentificador-c]"), "contraseña_C-TGS"); // descifra el objeto autenticacion
        this.setAutificacionCliente(autenticacionCliente);// guarda la Autentificacion del cliente descifrada


        AuthenticationServer.Ticket_TGS ticket_tgs = (AuthenticationServer.Ticket_TGS)
                AESUtils.desencriptarObjeto((SealedObject) peticion.get("[Ticket-tgs]"), "contraseñaTGS"); // descifra el ticket recibido
        this.setTicket_tgs(ticket_tgs);// guarda el ticket TGS descifrado que se recibio

        System.out.printf("Ticket TGS descifrado: %s \n\n", ticket_tgs);
        System.out.printf("Autenticacion Cliente descifrada: %s \n\n", autenticacionCliente);

        this.setId_cliente(autenticacionCliente.getId_cliente()); //obtiene el id del cliente de la autentificacion
        this.setAddress_cliente(autenticacionCliente.getAddress_cliente()); // obtiene el address de la autentificacion
        this.setId_servidor((String) peticion.get("[Id-v]"));// obtiene el id del serverService de la peticion

    }

    public void enviarRespuestaTicketAlCliente(OutputStream outputStream) throws Exception {
        HashMap<String, Object> respuestaTicket = this.crearRespuestaTicket(); // crea la respuesta a la peticion del cliente
        SealedObject respuestaCifrada = encriptarObjeto(respuestaTicket, ticket_tgs.getClave_Cliente_TicketGrantingServer()); // Cifra la respuesta con la Contraseña entre el cliente y el TGS
        Comunicaciones.enviarObjeto(outputStream, respuestaCifrada); // Envia el mensaje concatenado al cliente conteniendo el Ticket-v
        System.out.printf("\nRespuesta sin cifrar: %s", respuestaTicket);
        System.out.printf("\nRespuesta cifrada a enviar: %s", respuestaCifrada);
    }
//
//    public void setClave_cliente_ticketGrantingServer(Key clave_cliente_ticketGrantingServer) {
//        this.clave_cliente_ticketGrantingServer = clave_cliente_ticketGrantingServer;
//    }
//
//    public void setClave_cliente_servidor(Key clave_cliente_servidor) {
//        this.clave_cliente_servidor = clave_cliente_servidor;
//    }
//
//    public void setClave_serviceServer(Key clave_serviceServer) {
//        this.clave_serviceServer = clave_serviceServer;
//    }

    public void setId_servidor(String id_servidor) {
        this.id_servidor = id_servidor;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setAddress_cliente(InetAddress address_cliente) {
        this.address_cliente = address_cliente;
    }

    public HashMap<String, Object> crearRespuestaTicket() throws Exception {
        HashMap<String, Object> respuestaSolicitud = new HashMap<>();

        Ticket_servicio ticket_servidor = new Ticket_servicio("contraseñaClienteServidor", id_cliente, address_cliente, id_servidor, 5);

        respuestaSolicitud.put("[K-c_v]", ticket_servidor.getClave_cliente_servidor()); // una clave entre el Cliente y el Servidor (K c-v)
        respuestaSolicitud.put("[Id-v]", ticket_servidor.getId_servidor()); // id del cliente
        respuestaSolicitud.put("[TimeStamp-4]", ticket_servidor.getCreacion_ticket());
        respuestaSolicitud.put("[TiempoVida-4]", ticket_servidor.getTiempo_vida_ticket());

        SealedObject ticket_servidor_cifrado = encriptarObjeto(ticket_servidor, "contraseñaServidor"); // cifrar ticket-v con la contraseña del servidor
        respuestaSolicitud.put("[Ticket-v]", ticket_servidor_cifrado); // concatenar ticket-v Cifrado

        System.out.printf("\n[Ticket-v] cifrado y descifrado-> %s -> %s \n", ticket_servidor, ticket_servidor_cifrado);

        return respuestaSolicitud;
    }


    public static class Ticket_servicio implements Serializable {
        final String clave_cliente_servidor;
        final String id_cliente;
        final InetAddress address_cliente;
        final LocalDateTime creacion_ticket;
        final LocalDateTime tiempo_vida_ticket;
        final String id_servidor;

        public Ticket_servicio(String clave_cliente_servidor, String id_cliente, InetAddress address_cliente, String id_servidor, long tiempoVida) {
            this.clave_cliente_servidor = clave_cliente_servidor;
            this.id_cliente = id_cliente;
            this.address_cliente = address_cliente;
            this.id_servidor = id_servidor;
            this.creacion_ticket = LocalDateTime.now();
            this.tiempo_vida_ticket = creacion_ticket.plusMinutes(tiempoVida);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Ticket_servicio{");
            sb.append("clave_cliente_servidor=").append(clave_cliente_servidor);
            sb.append(", id_cliente='").append(id_cliente).append('\'');
            sb.append(", address_cliente=").append(address_cliente);
            sb.append(", creacion_ticket=").append(creacion_ticket);
            sb.append(", tiempo_vida_ticket=").append(tiempo_vida_ticket);
            sb.append(", id_servidor='").append(id_servidor).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String getClave_cliente_servidor() {
            return clave_cliente_servidor;
        }

        public String getId_cliente() {
            return id_cliente;
        }

        public InetAddress getAddress_cliente() {
            return address_cliente;
        }

        public LocalDateTime getCreacion_ticket() {
            return creacion_ticket;
        }

        public LocalDateTime getTiempo_vida_ticket() {
            return tiempo_vida_ticket;
        }

        public String getId_servidor() {
            return id_servidor;
        }

        public String getIp_cliente() {
            return address_cliente.getHostAddress();
        }
    }

}
