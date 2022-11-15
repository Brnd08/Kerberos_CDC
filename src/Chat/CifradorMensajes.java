package Chat;

import Utilities.EncriptadorBytes;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

public class CifradorMensajes {

    private static final String ALGORITMO_CIFRADO_MENSAJES = "AES/CBC/PKCS5Padding";
    private final PrivateKey llavePrivada;

    public CifradorMensajes(PrivateKey llavePrivada){
        this.llavePrivada = llavePrivada;
    }

    public String cifrarMensaje (String mensaje_a_cifrar){
        String mensajeCifrado = null;

        try{
            byte[] bytes_cifrados;
            byte[] bytes_mensaje = mensaje_a_cifrar.getBytes(StandardCharsets.UTF_8); // obtiene los bytes del mensaje para poder cifrarlos

            EncriptadorBytes encriptadorBytes = new EncriptadorBytes(ALGORITMO_CIFRADO_MENSAJES); // crea un objeto encriptador de bytes con el algoritmo de cifrado
            bytes_cifrados = encriptadorBytes.encriptarBytes(bytes_mensaje, llavePrivada); // llama al metodo encriptar bytes, y asigna los bytes que regresa a bytes cifrados
            mensajeCifrado = new String(bytes_cifrados, StandardCharsets.UTF_8); // crea un nuevo String a partir de l0s bytes cifrados del mensaje

        } catch (Exception e) {
            System.out.println("No se pudo encriptar el mensaje" + e.getMessage());
            e.printStackTrace();
        }
        return mensajeCifrado;
    }
}
