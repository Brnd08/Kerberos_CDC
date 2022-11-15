package Chat;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class GeneradorClaveSecreta {

    private static final String ALGORITMO_GENERADOR_LLAVE_PRIVADA = "PBKDF2WithHmacSHA256";
    private static final String salt = "randonSalt";


    public static SecretKey generarLlave(String clave_secreta) throws NoSuchAlgorithmException, InvalidKeySpecException {

        char[] caracteres_clave = clave_secreta.toCharArray();
        byte[] bytes_salt = salt.getBytes();

        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO_GENERADOR_LLAVE_PRIVADA); // crea un generador de llaves secretas con el algoritmo indicado

        KeySpec spec = new PBEKeySpec(caracteres_clave, bytes_salt, 65536, 256);
        // crea una configuracion de llave usando las letras de la claveSecreta

        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        return secret;
    }
}
