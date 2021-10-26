package uc3m.crypto.server;

import uc3m.crypto.security.DH;
import uc3m.crypto.server.model.DB;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private Set<UserThread> userThreads = new HashSet<>();
    private DB database;

    public Server() {
        database = DB.loadDatabase("src/uc3m/crypto/server/model/databaseFile");
        DB.saveDatabase(database);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
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
            DB.saveDatabase(database);
            System.out.println("Server Issue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void broadcast(Message message) {
        database.getHistory().add(message);
        DB.saveDatabase(database);
        for (UserThread user : userThreads) {
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }

    void removeUser(UserThread user) {
        if (user.getUserName() != null)
            broadcast(new Message("Server", "****  " + user.getUserName() + " has left.  ****", new Date()));
        userThreads.remove(user);
    }

    public User authenticate(String username, String hashedPassword) {
        if (database.getUsernames().contains(username)) {
            for (User user : database.getUsers()) {
                if (user.getUsername().equals(username) && user.getPassword().equals(hashedPassword)) {
                    return user;
                }
            }
            return null;
        }
        return null;
    }

    public User signUp(String username, String hashedPassword) {
        if (database.getUsernames().contains(username) || username.isBlank()) return null;
        User createdUser = new User(username, hashedPassword);
        database.getUsernames().add(username);
        database.getUsers().add(createdUser);
        DB.saveDatabase(database);
        return createdUser;
    }

}