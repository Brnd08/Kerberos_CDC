package Utilities;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

public class DesencriptadorBytes {



    private final Cipher descifrador;

    public DesencriptadorBytes(String algoritmo_de_cifrado)throws Exception {
        try {
            descifrador = Cipher.getInstance(algoritmo_de_cifrado);
        } catch (GeneralSecurityException e) {
            throw new Exception("No se pudo crear el cifrador con el algoritmo"  + algoritmo_de_cifrado);
        }
    }

    public byte[] descencriptarBytes(byte[] bytes_encriptados, Key llave_de_cifrado) throws Exception {
        byte[] bytes_desencriptados = null;
        descifrador.init(Cipher.DECRYPT_MODE, llave_de_cifrado); // Inicializa el cifrador en modo desencriptar, y se le pasa la llave para desencriptar
        bytes_desencriptados = descifrador.doFinal(bytes_encriptados); // Se le asigna a bytes_encriptados, los bytes encriptados que regresa el cifrador
        return bytes_desencriptados;
    }

}
