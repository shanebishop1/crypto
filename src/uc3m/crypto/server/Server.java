package uc3m.crypto.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private Set<UserThread> userThreads = new HashSet<>();

    public Server() {
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(5505)) {
            while (true) {
                Socket socket = serverSocket.accept();
                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException e) {
            System.out.println("Server Issue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    void broadcast(String message, UserThread exemptUser) {
        for (UserThread user : userThreads) {
            if (user != null && user != exemptUser) {
                user.sendMessage(message);
            }
        }
    }

    void removeUser(UserThread user) {
        userThreads.remove(user);
    }
}