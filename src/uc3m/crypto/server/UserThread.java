package uc3m.crypto.server;

import java.io.*;
import java.net.*;

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

            userName = reader.readLine();
            server.broadcast("****  " + userName + " has connected to the server.  ****");
            String clientMessage;
            String toSend = reader.readLine();

            while (toSend != null) {
                try {
                    if (toSend.equals("///LOGGING_OUT")) {
                        break;
                    }
                    clientMessage = "[" + userName + "]: " + toSend;
                    server.broadcast(clientMessage);
                    toSend = reader.readLine();
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
}