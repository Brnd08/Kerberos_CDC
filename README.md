# Kerberos_CDC
Kerberos CDC es una implementación usando solo librerias nativas de java del protocolo de distribuición de claves Kerberos. 
     
# Diseño del sistema 
Este proyecto fue basado en la descripción del protocolo Kerberos descrito en la segunda version del libro "Fundamentos de seguridad en Redes" escrito por Wiliam Staling.

![image](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/31d3c7b0-2b66-4aa2-bd83-58904cc18144)

La implementación completa de este sistema sigue el siguiente proceso: 


### I. INTERCAMBIO DE SERVICIO DE AUTENTIFICACION PARA OBTENER UN TGT (Ticket Granting Ticket)
![image](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/e8d13aec-0354-4640-a21a-1214652d5194)
  1. El Cliente(C)  envia mensaje al Authentication Server (AS), solicitando acceso al Ticket Granting Server (TGS), el mensaje incluye una concatenacion de los siguientes datos: Id del Cliente (IDc), Id del Ticket Granting Server (IDtgs) , y una marca de tiempo (TS1) de el momento en que se creó la solicitud  
  2. El Authentication Server (AS), crea un ticket(Ticket tgs) que contiene los siguientes datos concatenados:  una clave entre el Cliente y el Ticket Granting Server (K c-tgs),  el Id del cliente (IDc), el adress o ip de la maquina Cliente (ADc), el Id del Ticket Granting Server (ID tgs), una marca de tiempo (TS2) refiriendose a la creacion de este ticket tgs , y finalmente el tiempo de vida del Ticket (Tiempo de vida 2). ESTE TICKET ESTA CIFRADO CON LA CONTRASEÑA/CLAVE DEL Ticket Granting Server (K tgs)
  3. El Autentication Server (AS) obtiene  la contraseña del cliente desde la base de datos, usando el (IDc), y cifra con ella un mensaje que envia de vuelta al Cliente  (C), el cual contiene los siguientes datos concatenados: la clave entre el Cliente y el Ticket Granting Server (K c-tgs) anteriormente mencionada, el Id del Ticket Granting Server (IDtgs),  la marca de tiempo de creacion del Ticket tgs (TS2), el tiempo del vida del Ticket tgs (Tiempo de vida 2)y el ticket tgs previamente creado (Ticket tgs)

### II. INTERCAMBIO DE TGT (Ticket Granting Ticket)
![image](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/03947a5e-aeb3-4897-b25c-238fa27e5924)
  1. El Cliente (C), solicita el usuario su contraseña, para poder descifrar el mensaje recibido desde el Autentication Server (AS) , y crea un Autenticador propio (Autenticador c), el cual esta cifrado con la clave entre el Cliente y el Ticket Granting Server (K c-tgs), indicando la siguiente informacion,:  Id del Cliente (IDc), la ip de la maquina Cliente (ADc), y una nueva marca de tiempo de este Autenticador (TS3)
  2. El cliente  envia un mensaje concatenado hacia el Ticket Granting Server (TGS), el cual contiene: Id del Servidor el cual contiene el servicio que necesita (IDv) , el ticket que recibio del AS (Ticket tgs), y el Autentificador propio del Cliente anteriormente creado (Autentificador c)
  3. El ticket Granting Server (TGS) descifra el mensaje recibido y comprueba que coincidan el id (ID c) y la Ip del cliente (ADc)  con el id(IDc) y la ip (ADc) especificada en el Autenticador del Cliente (Autenticador c). Si todo esta correcto crea y cifra con la clave del Servidor(K v), un ticket reutilizable para el mismo servidor (Ticket v) , el cual contiene lo siguiente : una clave entre el Cliente y el Servidor (K c-v), el Id del cliente (IDc), la Ip del cliente(ADc), el Id del servidor (IDv), una nueva marca de tiempo para este ticket (TS4) junto con su tiempo de vida (Tiempo de vida 4)
  4. El Ticket Granting Server (TGS) responde al Cliente con un mensaje cifrado con la clave entre el Cliente y el Ticket Granting Server (K c-tgs) el cual contiene: una Clave entre el cliente(c) y el servidor que concede el servicio (V)  (K c-v), el id del servidor que concede el Servicio (IDv), la marca de tiempo del ticket del servidor (Ticket v) y el Ticket para el servidor que brinda el servicio (Ticket v)
### III. INTERCAMBIO DE AUTENTIFICACIÓN CLIENTE/SERVIDOR: PARA OBTENER UN SERVICIO
![image](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/c05d7430-7e1f-4de8-9e4d-7e1d792fc0d6)
  1. El Cliente(C) descifra  el mensaje recibido desde el TGS con la clave entre el Cliente y el TGS(K c-tgs),obtiene la clave que usara para comunicarse con el servidor(K c-s), y crea un nuevo Autentificador de cliente(Autentificador C2), que contiene:   Id del Cliente (IDc), la ip de la maquina Cliente (ADc), y una nueva marca de tiempo para este Autenticador (TS5)
  2. El cliente (C) envía  un mensaje cifrado con la clave que recibió entre el Cliente y El servidor (K c-v) el cual contiene: el Ticket para el servidor que se recibió (Ticket v) y el nuevo Autentificador  del cliente recién creado (Autentificador c2)
  3. El Servidor descifra el Mensaje recibido con la clave entre el Cliente y el Servidor (K c-v) comprueba que el Id del Cliente , Ip del Cliente indicados en el Ticket se servidor que recbió (Ticket v ) coincidan con las que se especifican en el Autentificador del Cliente (Autentificador c), si es así el servidor concede acceso al servicio, y  devuelve al cliente (C), una nueva marca de tiempo incrementada(TS5 +1), la cual esta cifrada con la Clave entre el servidor  y el cliente (K c-v)

### Diagrama de Clases
![Kerberos diagrama](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/f15d131f-5c71-40e3-84d9-a9ef1d562203)

# Ejecución
Para ejecutar este proyecto de forma local clona este repositorio. ``` git clone https://github.com/Brnd08/Kerberos_CDC ```

Crea cuatro instancias de la consola en la carpeta `src`, y ejecuta en orden lo siguiente (cada consola ejecutará un servidor distinto).
```
javac KerberosEntities/AutenticationServer.java
java KerberosEntities.AutenticationServer
```
```
javac KerberosEntities/TicketGrantingServer.java
java KerberosEntities.Client
```
```
javac KerberosEntities/ServiceServer.java
java KerberosEntities.Client
```
```
javac KerberosEntities/Client.java
java KerberosEntities.Client
```
NOTA: El programa admite distintas Ips para cada servidor, si lo quieres correr en local solo usa el localhost (127.0.0.1) pero cada servidor debera estar funcionando en puertos distinto

Despues deberías poder ver algo como esto: ![image](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/c47b480e-9120-4023-bd99-60e76c16ff74)

Despues de seguir los pasos que indica el programa del cliente ejecutará todo el proceso y verás como recibes el servicio (El servicio es un ASCCI art).![image](https://github.com/Brnd08/Kerberos_CDC/assets/93061195/6cd20773-752c-4da1-a35e-5c7e4b3ce6f7)

## Tutorial 

Tutorial sobre la ejecución y funcionamiento del sistema.

https://github.com/Brnd08/Kerberos_CDC/assets/93061195/cb52c3c9-9a97-4a64-9cfb-fc7b02026ce7

