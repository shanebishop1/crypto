package uc3m.crypto.server;

import uc3m.crypto.security.AES;
import uc3m.crypto.server.model.Message;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Server {
    private Set<UserThread> userThreads = new HashSet<>();
    private SecretKey key;
    private IvParameterSpec iv;

    public Server() {
    }

    public void start() {
        try {
            key = AES.generateKey(128);
            iv = AES.generateIv();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
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

    void broadcast(String message) {
        for (UserThread user : userThreads) {
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }

    void broadcast(Message message) {
        for (UserThread user : userThreads) {
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }

    void removeUser(UserThread user) {
        broadcast("**** " + user.getUserName() + " has left. ****");
        userThreads.remove(user);
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public IvParameterSpec getIv() {
        return iv;
    }

    public void setIv(IvParameterSpec iv) {
        this.iv = iv;
    }
}