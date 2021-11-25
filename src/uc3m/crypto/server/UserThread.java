package uc3m.crypto.server;

import uc3m.crypto.security.AES;
import uc3m.crypto.security.PBKDF2;
import uc3m.crypto.security.SHA;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.naming.AuthenticationException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class UserThread extends Thread {
    private final SecretKey key;
    private final IvParameterSpec iv;
    private Socket socket;
    private Server server;
    private PrintWriter writer;
    private String userName;
    private User user;

    public UserThread(Socket socket, Server server, byte[] secret) {
        this.socket = socket;
        this.server = server;
        key = AES.generateKeyFromSecret(secret);
        iv = AES.generateIvFromSecret(secret); //generates AES parameters from the Diffie Hellman generated secret
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            Message clientMessage, receivedMessage;
            boolean isSignUpInstance = false;
            String encryptedMessage = reader.readLine(); //INITIAL INSTRUCTION, for login or sign up
            receivedMessage = new Message(AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv), key); //checking HMAC
            String initialInstruction = receivedMessage.getContent();
            if (initialInstruction.equals("SignMeUp")) isSignUpInstance = true;

            encryptedMessage = reader.readLine(); //USERNAME
            receivedMessage = new Message(AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv), key);
            String username = receivedMessage.getContent();

            encryptedMessage = reader.readLine(); //PASSWORD
            receivedMessage = new Message(AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv), key);
            String password = receivedMessage.getContent();
            if (isSignUpInstance) {
                long start = System.currentTimeMillis();
                user = server.signUp(username, password);
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                System.out.println("Sign up server execution time: " + timeElapsed + "ms");
            }
            else {
                long start = System.currentTimeMillis();
                user = server.authenticate(username, password);
                long finish = System.currentTimeMillis();
                long timeElapsed = finish - start;
                System.out.println("Login server execution time: " + timeElapsed + "ms");
            }

            if (user != null) { //success
                if (isSignUpInstance) sendMessage("SIGNED UP"); //info for the user, that they have been successful
                else sendMessage("ACCEPTED");
                userName = username;
                server.broadcast(new Message("server", "****  " + userName + " has connected to the server.  ****", new Date()));
            } else { //info for the user, that they have NOT been successful
                if (isSignUpInstance) {
                    sendMessage("INVALID SIGNUP"); //these messages are just information for the user, they still get removed serverside
                    throw new AuthenticationException("Invalid signup");
                } else {
                    sendMessage("DENIED");
                    throw new AuthenticationException("Unauthorized");
                }
            }

            encryptedMessage = reader.readLine();
            while (encryptedMessage != null) { //main Thread loop
                try {
                    receivedMessage = new Message(AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv), key); //decrypt
                    String plainMessage = receivedMessage.getContent();
                    if (plainMessage.equals("///LOGGING_OUT")) {
                        break;
                    }
                    //create message object, only the server can objectively say that it is truly the user with the userName
                    clientMessage = new Message(userName, plainMessage, receivedMessage.getDateSent());
                    clientMessage.setSig(receivedMessage.getSig());
                    server.broadcast(clientMessage);
                } catch (Exception ex) {
                    System.out.println("Server decryption: " + ex.getMessage());
                }
                try {
                    encryptedMessage = reader.readLine();
                } catch (SocketException ex) {
                    System.out.println(ex.getMessage());
                    server.removeUser(this);
                    socket.close();
                    break;
                }
            }
            server.removeUser(this); //on thread finish
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) { //the user gets removed here in the case of denial of access because of authentication
            server.removeUser(this);
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    void sendMessage(String message) {
        sendMessage(new Message("server", message, new Date()));
    }

    void sendMessage(Message message) { //encrypt and send message
        try {
            String encryptedMessage = AES.encrypt("AES/CBC/PKCS5Padding", message.setHmac(getKey()).toString(), key, iv);
            writer.println(encryptedMessage);
        } catch (Exception ex) {
            System.out.println("Server decryption: " + ex.getMessage());
        }
    }

    public SecretKey getKey() {
        return key;
    }
}