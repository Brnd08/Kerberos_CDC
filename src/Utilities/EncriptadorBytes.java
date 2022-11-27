package Utilities;


import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.Key;

public class EncriptadorBytes {
    Cipher cifrador;

    public EncriptadorBytes(String algoritmo_de_cifrado)throws Exception {
        try {
            cifrador = Cipher.getInstance(algoritmo_de_cifrado);
        } catch (GeneralSecurityException e) {
            throw new Exception("No se pudo crear el cifrador con el algoritmo"  + algoritmo_de_cifrado);
        }
    }

    public byte[] encriptarBytes(byte[] bytes_a_encriptar, Key llave_de_cifrado) throws Exception {
        byte[] bytes_encriptados = null;
            cifrador.init(Cipher.ENCRYPT_MODE, llave_de_cifrado); // Inicializa el cifrador en modo encriptar, y se le pasa la llave de cifrado
            bytes_encriptados = cifrador.doFinal(bytes_a_encriptar); // Se le asigna a bytes_encriptados, los bytes encriptados que regresa el cifrador
        return bytes_encriptados;
    }

}
