package Utilities;

import java.io.*;
import java.util.Base64;

public class Comunicacion {

    public static void enviarMensaje(String mensaje_enviar, OutputStream outputStream){
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        printWriter.println(mensaje_enviar);
        printWriter.flush();
        return;
    }
    public static String recibirMensaje(InputStream inputStream) throws IOException {
        BufferedReader socketInputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
        String message = socketInputStreamReader.readLine();
        return message;
    }


    public static Object recibirObjeto(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object objetoRecibido = objectInputStream.readObject();
        return objetoRecibido;
    }

    public static void enviarObjeto(OutputStream outputStream, Object objetoEnviar) throws IOException{
        ObjectOutputStream objectOnputStream = new ObjectOutputStream(outputStream);
        objectOnputStream.writeObject(objetoEnviar);
        objectOnputStream.flush();
    }

    public static String encodeBytes (byte[] bytesToEncode){
        return Base64.getEncoder().encodeToString(bytesToEncode);
    }
    public static byte[] decodeString(String stringToDecode){
        return Base64.getDecoder().decode(stringToDecode);
    }
}
