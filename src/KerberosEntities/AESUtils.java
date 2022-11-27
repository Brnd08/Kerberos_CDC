package KerberosEntities;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AESUtils {
    static String salt = "12345678";
    static String ObjectsCipherAlg = "AES/CBC/PKCS5Padding";

    public static SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static SecretKey getKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        iv = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        return new IvParameterSpec(iv);
    }


    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static SealedObject encryptObject(String algorithm, Serializable object, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        SealedObject sealedObject = new SealedObject(object, cipher);
        return sealedObject;
    }

    public static Serializable decryptObject(String algorithm, SealedObject sealedObject, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
        return unsealObject;
    }

    public static SealedObject encryptObject(Serializable object, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ObjectsCipherAlg);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        SealedObject sealedObject = new SealedObject(object, cipher);
        return sealedObject;
    }

    public static Serializable decryptObject(SealedObject sealedObject, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance(ObjectsCipherAlg);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
        return unsealObject;
    }


    /**
     * Devuelve un objeto cifrado a partir de una contraseña
     *
     * @param objeto_cifrar El objeto que se desea cifrar, debe ser algun objeto que implemente Serializable
     * @param pass          String que indica la contraseña con la que se va a cifrar
     * @return SealedObject - Representacion cifrada del objeto
     * @throws Exception si no se puedo cifrar el objeto
     */
    public static SealedObject encriptarObjeto(Serializable objeto_cifrar, String pass) throws Exception {
        SecretKey claveSecreta = AESUtils.getKeyFromPassword(pass);
        IvParameterSpec iv = AESUtils.generateIv();
        SealedObject respuestaCifrada = AESUtils.encryptObject(objeto_cifrar, claveSecreta, iv);
        return respuestaCifrada;
    }

    /**
     * Cifra un objeto usando la clave secreta dadda
     *
     * @param objeto_cifrar Objeto serializable a cifrar
     * @param claveSecreta  SecretKey para cifrado
     * @return SealedObject representando el objeto Cifrado
     * @throws Exception
     */
    public static SealedObject encriptarObjeto(Serializable objeto_cifrar, SecretKey claveSecreta) throws Exception {

        IvParameterSpec iv = AESUtils.generateIv();
        SealedObject respuestaCifrada = AESUtils.encryptObject(objeto_cifrar, claveSecreta, iv);
        return respuestaCifrada;
    }

    /**
     * Descifra un SealedObject en un Serializable que puede ser casteado posteriormente
     *
     * @param objeto_cifrado SealedObject representando el objeto cifrado
     * @param pass           String conteniendo la contraseña de cifrado
     * @return Serializable
     * @throws Exception si no se pudo descifrar el objeto
     */
    public static Serializable desencriptarObjeto(SealedObject objeto_cifrado, String pass) throws Exception {
        SecretKey clave = AESUtils.getKeyFromPassword(pass);
        IvParameterSpec iv = AESUtils.generateIv();
        Serializable objecto_descifrado = AESUtils.decryptObject(objeto_cifrado, clave, iv);
        return objecto_descifrado;
    }

}
