package Utilities;

import java.io.*;
import java.util.Base64;

public class Comunicacion {

    /**
     * Envia un mensaje utilizando el flujo de salida dado
     * @param mensaje_enviar
     * @param outputStream
     */

    public static void enviarMensaje(String mensaje_enviar, OutputStream outputStream){
        // crea el print Writer
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        // escribe una linea en el buffer
        printWriter.println(mensaje_enviar);
        // vacia o envia el buffer
        printWriter.flush();
        return;
    }

    /**
     * Envia mensaje utilizado un print writer dado
     * @param mensaje_enviar
     * @param printWriter
     */
    public static void enviarMensaje(String mensaje_enviar, PrintWriter printWriter){
        printWriter.println(mensaje_enviar);
        printWriter.flush();
        return;
    }

    /**
     * Espera un mensaje entrante en el input stream dado
     * @param inputStream
     * @return
     * @throws IOException
     */

    public static String recibirMensaje(InputStream inputStream) throws IOException {
        // crea el bufferedReader para el flujo de entrada
        BufferedReader socketInputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
        // lee el mensaje entrante
        String message = socketInputStreamReader.readLine();
        // retorna el mensaje
        return message;
    }

    /**
     * Espera un mensaje entrante en utilizando un buffered reader
     * @param socketInputStreamReader
     * @return
     * @throws IOException
     */
    public static String recibirMensaje(BufferedReader socketInputStreamReader) throws IOException {
        // lee el mensaje y lo retorna
        String message = socketInputStreamReader.readLine();
        return message;
    }

    /**
     * Recibe un mensaje en el flujo de entrada
     * @param inputStream
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object recibirObjeto(InputStream inputStream) throws IOException, ClassNotFoundException {
        // Crea un flujo de entrada de objetos
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        // recibe el objeto entrante
        Object objetoRecibido = objectInputStream.readObject();
        // retorna el objeto
        return objetoRecibido;
    }

    /**
     * Recive un objeto en el flujo dado
     * @param outputStream
     * @param objetoEnviar
     * @throws IOException
     */
    public static void enviarObjeto(OutputStream outputStream, Object objetoEnviar) throws IOException{
        // crea flujo de entrada de objetos
        ObjectOutputStream objectOnputStream = new ObjectOutputStream(outputStream);
        // escribe el objeto dado en el flujo
        objectOnputStream.writeObject(objetoEnviar);
        // envia el objeto
        objectOnputStream.flush();
    }

    /**
     * Convierte o codifica un arrreglo de bytes en su representacion en String
     * @param bytesToEncode
     * @return
     */
    public static String encodeBytes (byte[] bytesToEncode){
        return Base64.getEncoder().encodeToString(bytesToEncode);
    }

    /**
     * Convierte o decodifica un String en un arreglo de bytes
     * @param stringToDecode
     * @return
     */
    public static byte[] decodeString(String stringToDecode){
        return Base64.getDecoder().decode(stringToDecode);
    }
}
