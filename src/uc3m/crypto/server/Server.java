package uc3m.crypto.server;

import uc3m.crypto.security.AES;
import uc3m.crypto.security.DH;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Server {
    private Set<UserThread> userThreads = new HashSet<>();

    public Server() {
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(5505)) {
            while (true) {
                Socket socket = serverSocket.accept();
                DH dh = new DH();
                byte[] secret = dh.init(socket, true);
                UserThread newUser = new UserThread(socket, this, secret);
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

    void broadcast(Message message) {
        for (UserThread user : userThreads) {
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }

    void removeUser(UserThread user) {
        broadcast(new Message("Server", "****  " + user.getUserName() + " has left.  ****", new Date()));
        userThreads.remove(user);
    }

    public User autentificate(String username, String password) {
        //HERE COMES THE DATABASE LOGIC
        return new User(username, password);
    }
}