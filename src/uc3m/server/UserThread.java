package uc3m.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class UserThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter writer;

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

            String userName = reader.readLine();
            server.broadcast("****  " + userName + " has connected to the server.  ****", this);
            String clientMessage;
            String toSend = reader.readLine();

            while (toSend != null) {
                clientMessage = "[" + userName + "]: " + toSend;
                server.broadcast(clientMessage, this);
                toSend = reader.readLine();
            }

            server.removeUser(this);
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String message) {
        writer.println(message);
    }
}