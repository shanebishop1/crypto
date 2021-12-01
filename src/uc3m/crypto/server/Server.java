package uc3m.crypto.server;

import uc3m.crypto.security.DH;
import uc3m.crypto.security.PBKDF2;
import uc3m.crypto.security.X509;
import uc3m.crypto.server.model.DB;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Server { //Central server, hosts all clients in one chat room
    private Set<UserThread> userThreads = new HashSet<>(); //one thread per user
    private DB database; //DB for storing users, message history
    private PrivateKey privateKey;

    public Server() {
        database = DB.loadDatabase("./databaseFile");
        DB.saveDatabase(database);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() { //server listens on the socket and for each user creates new UserThread
        X509.setPath("C:\\Users\\lukyb\\Documents\\openssl\\");
        X509.setPath("./openssl/");
        privateKey = X509.loadPrivateKey("server");
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

    void broadcast(Message message) { //sends a message to all users
        database.getHistory().add(message);
        DB.saveDatabase(database);
        for (UserThread user : userThreads) {
            if (user != null) {
                user.sendMessage(message);
            }
        }
    }

    void sendPrivateMessage(Message message, String username) {
        database.getHistory().add(message);
        DB.saveDatabase(database);
        for (UserThread user : userThreads) {
            if (user != null && user.getUserName().equals(username)) {
                user.sendMessage(message);
            }
        }
    }


    void removeUser(UserThread user) { //removes the specific UserThread, messages other users
        String username = user.getUserName();
        if (userThreads.remove(user) && user.getUserName() != null) {
            Message msg = new Message("server", "****  " + username + " has left.  ****", new Date());
            msg.sign(privateKey);
            broadcast(msg);
        }
    }

    public User authenticate(String username, String hashedPassword) { //login authentification
        if (database.getUsernames().contains(username)) { //check if username present
            for (UserThread userThread : userThreads) { //check if user already in the server
                if (userThread.getUserName() != null && userThread.getUserName().equals(username)) {
                    return null;
                }
            }
            User user = database.getUsers().get(username);
            if (user == null) {
                return null;
            }
            if ( PBKDF2.defaultHash(hashedPassword, user.getSalt()).equals(user.getPassword()) ) {
                return user;
            }
            return null;
        }
        return null;
    }

    public User signUp(String username, String hashedPassword) { //signup logic
        if (database.getUsernames().contains(username) || username.isBlank()) return null; //user already signed up or username empty
        String salt = PBKDF2.generateSalt();
        hashedPassword = PBKDF2.defaultHash(hashedPassword, salt);
        User createdUser = new User(username, hashedPassword, salt);
        database.getUsernames().add(username);
        database.getUsers().put(username, createdUser);
        DB.saveDatabase(database);
        return createdUser;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}