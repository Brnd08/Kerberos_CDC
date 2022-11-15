package Chat;

import Utilities.DesencriptadorBytes;
import Utilities.EncriptadorBytes;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

public class DescifradorMensajes {

    private static final String ALGORITMO_CIFRADO_MENSAJES = "AES/CBC/PKCS5Padding";
    private final PrivateKey llavePrivada;

    public DescifradorMensajes(PrivateKey llavePrivada){
        this.llavePrivada = llavePrivada;
    }

    public String descifrarMensaje(String mensaje_a_descifrar){
        String mensaje_descifrado = null;

        try{
            byte[] bytes_descifrados;

            byte[] bytes_mensaje = mensaje_a_descifrar.getBytes(StandardCharsets.UTF_8); // obtiene los bytes del mensaje para poder descifrarlos

            DesencriptadorBytes desencriptadorBytes = new DesencriptadorBytes(ALGORITMO_CIFRADO_MENSAJES); // crea un objeto desencriptador de bytes con el algoritmo de cifrado
            bytes_descifrados = desencriptadorBytes.descencriptarBytes(bytes_mensaje, llavePrivada); // llama al metodo desencriptar bytes, y asigna los bytes que regresa a bytes_cifrados

            mensaje_descifrado = new String(bytes_descifrados, StandardCharsets.UTF_8); // crea un nuevo String a partir de l0s bytes cifrados del mensaje

        } catch (Exception e) {
            System.out.println("No se pudo encriptar el mensaje" + e.getMessage());
            e.printStackTrace();
        }
        return mensaje_descifrado;
    }
}
