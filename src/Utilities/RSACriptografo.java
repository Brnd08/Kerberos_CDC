package Utilities;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class RSACriptografo {
    final String algoritmo;

    public RSACriptografo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public String cifrarString (String stringCifrar, Key llave_cifrado) throws Exception {

        byte[] bytesClave = stringCifrar.getBytes(StandardCharsets.UTF_8);
        EncriptadorBytes encriptadorBytes = new EncriptadorBytes(algoritmo);
        byte[] bytesEncriptados = encriptadorBytes.encriptarBytes(bytesClave, llave_cifrado);
        String stringCifrado = Base64.getEncoder().encodeToString(bytesEncriptados);
        return stringCifrado;
    }

    public String descifrarString (String stringDescifrar, Key llave_cifrado) throws Exception {
        byte[] bytesClave = stringDescifrar.getBytes(StandardCharsets.UTF_8);
        DesencriptadorBytes desencriptadorBytes = new DesencriptadorBytes(algoritmo);
        byte[] bytesDesencriptados = desencriptadorBytes.descencriptarBytes(bytesClave, llave_cifrado);
        String stringCifrado = new String(bytesDesencriptados, StandardCharsets.UTF_8);
        return stringCifrado;
    }


}
