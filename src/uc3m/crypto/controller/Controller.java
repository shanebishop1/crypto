package uc3m.crypto.controller;

import uc3m.crypto.model.DB;
import uc3m.crypto.model.User;
import uc3m.crypto.view.Login;
import uc3m.crypto.view.UI;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Random;
import javax.swing.WindowConstants;


public class Controller {
    private static Random random;
    private static Login login;
    private static UI ui;
    private static DB db;
    private static User user;
    private static SendThread sendThread;

    public Controller() {
        random = new Random();
        db = new DB();

        login = new Login(this);
        login.setVisible(true);
        login.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ui = new UI(this);
        ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
    }

    public void connectServer() {
        try {
            Socket socket = new Socket("localhost", 5505);
            ui.process("Connected to server.");
            new ReceiveThread(socket, this).start();
            sendThread = new SendThread(socket, this);
        } catch (UnknownHostException e) {
            ui.process("Unknown Host: " + e.getMessage());
            System.out.println("Unknown Host: " + e.getMessage());
        } catch (IOException e) {
            ui.process("I/O Exception: " + e.getMessage());
            System.out.println("I/O Exception: " + e.getMessage());
        }
    }

    public void login() {
        System.out.print("Username: ");
        Scanner input = new Scanner(System.in);
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();
        login(username, password);
    }

    public void login(String username, String password) {
        User currentUser = db.searchUsernamePassword(username, password);
        long randLong = random.nextLong();

        if (currentUser != null) {
            if (login != null) {
                login.setText("Login successful, here is your random long: " + randLong);
                login.dispose();
            }
            user = currentUser;
            ui.setVisible(true);
            connectServer();
            System.out.println("Login successful, here is your random long: " + randLong);
        } else {
            if (login != null) {
                login.setText("Login unsuccessful");
            }
            System.out.println("Login unsuccessful");
        }
    }

    public User getUser() {
        return user;
    }

    public UI getUI() {
        return ui;
    }

    public SendThread getSendThread() {
        return sendThread;
    }
}
