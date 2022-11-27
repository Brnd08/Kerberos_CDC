package Chat;
import Utilities.Comunicacion;
import Utilities.DesencriptadorBytes;
import Utilities.EncriptadorBytes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;

public class AESCriptografo {
    private static final String ALGORITMO = "AES/ECB/PKCS5Padding";

    private SecretKeySpec secretKeySpec;

    public AESCriptografo(String llaveSecreta) throws Exception {
        this.setLlave(llaveSecreta);
    }

    private void setLlave(final String myKey) {
        this.secretKeySpec = StringToSecretKey(myKey);
    }

    public String desencriptarMensaje(final String strToEncrypt) throws Exception {
        String mensajeDesencriptado = null;
        DesencriptadorBytes desencriptadorBytes = new DesencriptadorBytes(ALGORITMO);
        try {

            byte[] bytesCifrado = Comunicacion.decodeString(strToEncrypt);
            byte[] bytesDescifrados =
                    desencriptadorBytes.descencriptarBytes(bytesCifrado, this.secretKeySpec);
            mensajeDesencriptado = Comunicacion.encodeBytes(bytesDescifrados);
        } catch (Exception e) {
            System.out.println("Error al desencriptar el mensaje : " + e.toString());
            e.printStackTrace();
        }
        return mensajeDesencriptado;
    }

    public String encriptarMensaje(final String cadenaEncriptar) throws Exception {
        String mensajeEncriptado = null;

        EncriptadorBytes encriptador = new EncriptadorBytes(ALGORITMO);
        try {

            byte[] bytesMensaje = Comunicacion.decodeString(cadenaEncriptar);
            byte[] bytesCifrados = encriptador.encriptarBytes(bytesMensaje, this.secretKeySpec);
            mensajeEncriptado = Comunicacion.encodeBytes(bytesCifrados);
        } catch (Exception e) {
            System.out.println("Error al encriptar el mensaje : " + e.toString());
            e.printStackTrace();
        }
        return mensajeEncriptado;
    }



    public SecretKeySpec StringToSecretKey(String stringSecKey){
        // instancía un objeto ClaveSecreta
        SecretKeySpec secretKey = null;
        try{
            //obtiene los bytes de la clave secreta
            byte[] byteSecKey = stringSecKey.getBytes(StandardCharsets.UTF_8);
            // convierte los bytes de clave secreta a un arreglo de 16 bytes o 128 bits
            byteSecKey = Arrays.copyOf(byteSecKey, 16);
            // asigna valor al objeto ClaveSecreta
            secretKey = new SecretKeySpec(byteSecKey, ALGORITMO.split("/")[0]);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return secretKey;
    }
}