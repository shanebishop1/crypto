package uc3m.crypto.server;

import uc3m.crypto.security.AES;
import uc3m.crypto.server.model.Message;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Date;

public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;
    private String userName;

    public UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            sendMessage(Base64.getEncoder().encodeToString(server.getKey().getEncoded()));
            sendMessage(Base64.getEncoder().encodeToString(server.getIv().getIV()));

            userName = reader.readLine();
            server.broadcast(new Message("Server", "****  " + userName + " has connected to the server.  ****", new Date()));
            Message clientMessage;
            String encryptedMessage = reader.readLine();

            while (encryptedMessage != null) {
                try {
                    String plainMessage = AES.decrypt("AES/CBC/PKCS5Padding", encryptedMessage, server.getKey(), server.getIv());
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
        }
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    void sendMessage(String message) {
        writer.println(message);
    }

    void sendMessage(Message message) {
        try {
            String encryptedMessage = AES.encrypt("AES/CBC/PKCS5Padding", message.toString(), server.getKey(), server.getIv());
            writer.println(encryptedMessage);
        } catch (Exception ex) {
            System.out.println("Server decryption: " + ex.getMessage());
        }
    }
}