package Chat;
import Utilities.Comunicacion;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCriptografo {

    private static SecretKeySpec secretKeySpec;
    private static byte[] llave;

    public static void setLlave(final String myKey) {
        MessageDigest sha = null;
        try {
            llave = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            llave = sha.digest(llave);
            llave = Arrays.copyOf(llave, 16);
            secretKeySpec = new SecretKeySpec(llave, "AES");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encriptarMensaje(final String strToEncrypt, final String secret) {
        try {
            setLlave(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            String encriptado =
                    Comunicacion.encodeBytes((cipher.doFinal(strToEncrypt.getBytes("UTF-8"))));
            return encriptado;
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String desencriptarMensaje(final String cadenaDesencriptar, final String secret) {
        try {
            setLlave(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            String desEncriptado = new String(cipher.doFinal(Comunicacion.decodeString(cadenaDesencriptar)));
            return desEncriptado;
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}