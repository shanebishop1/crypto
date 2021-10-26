package uc3m.crypto.server;

import uc3m.crypto.security.AES;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.naming.AuthenticationException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;
    private String userName;
    private User user;
    private final SecretKey key;
    private final IvParameterSpec iv;

    public UserThread(Socket socket, Server server, byte[] secret) {
        this.socket = socket;
        this.server = server;
        key = AES.generateKeyFromSecret(secret);
        iv = AES.generateIvFromSecret(secret);
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            Message clientMessage;
            String encryptedMessage = reader.readLine(); //USERNAME
            String username = AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv);
            encryptedMessage = reader.readLine(); //PASSWORD
            String password = AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv);
            user = server.autentificate(username, password);
            if (user != null) {
                sendMessage("ACCEPTED");
                userName = username;
                server.broadcast(new Message("Server", "****  " + userName + " has connected to the server.  ****", new Date()));
            }
            else {
                sendMessage("DENIED");
                throw new AuthenticationException("Unauthorized");
            }

            encryptedMessage = reader.readLine();
            while (encryptedMessage != null) {
                try {
                    String plainMessage = AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, key, iv);
                    if (plainMessage.equals("///LOGGING_OUT")) {
                        break;
                    }
                    clientMessage = new Message(userName, plainMessage, new Date());
                    server.broadcast(clientMessage);
                } catch (Exception ex) {
                    System.out.println("Server decryption: " + ex.getMessage());
                }
                try {
                    encryptedMessage = reader.readLine();
                }
                catch (SocketException ex) {
                    System.out.println(ex.getMessage());
                    server.removeUser(this);
                    socket.close();
                    break;
                }
            }
            server.removeUser(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
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
        sendMessage(new Message("Server", message, new Date()));
    }

    void sendMessage(Message message) {
        try {
            String encryptedMessage = AES.encrypt("AES/CBC/PKCS5Padding", message.toString(), key, iv);
            writer.println(encryptedMessage);
        } catch (Exception ex) {
            System.out.println("Server decryption: " + ex.getMessage());
        }
    }
}