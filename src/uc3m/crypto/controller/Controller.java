package uc3m.crypto.controller;

import uc3m.crypto.model.DB;
import uc3m.crypto.model.User;
import uc3m.crypto.view.Login;
import uc3m.crypto.view.Messaging;
import uc3m.crypto.view.UI;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.WindowConstants;


public class Controller {
    private DB db;
    private Random random;
    private Login login;
    private Messaging ui;
    private volatile User user;
    private SendThread sendThread;
    private ConnectServer connectThread;

    private int targetPort;
    private String targetHostName;

    public Controller() {
        random = new Random();
        db = new DB();
        login = new Login(this);

        targetHostName = "localhost";
        targetPort = 7680;
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
    }

    public void connectServer() {
        if (connectThread != null) {
            connectThread.interrupt();
        }
        connectThread = new ConnectServer(this);
        connectThread.start();
    }

    class ConnectServer extends Thread {
        private Controller controller;
        public ConnectServer(Controller controller) {
            this.controller = controller;
        }
        public void run() {
            while (controller.getUser() != null || isInterrupted()) {
                try {
                    Socket socket = new Socket(targetHostName, targetPort);
                    ui.writeLine("Connected to server.");
                    new ReceiveThread(socket, controller).start();
                    sendThread = new SendThread(socket, controller);
                    break;
                } catch (UnknownHostException e) {
                    ui.writeLine("Unknown Host: " + e.getMessage());
                    System.out.println("Unknown Host: " + e.getMessage());
                } catch (IOException e) {
                    ui.writeLine("I/O Exception: " + e.getMessage());
                    System.out.println("I/O Exception: " + e.getMessage());
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception ex) {
                    ui.writeLine("Sleep error: " + ex.getMessage());
                }
            }
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
        User currentUser = this.db.searchUsernamePassword(username, password);
        long randLong = random.nextLong();

        if (currentUser != null) {
            if (login != null) {
                login.setText("Login successful, here is your random long: " + randLong);
                login.dispose();
            }
            user = currentUser;
            ui = new Messaging(this);
            ui.setUsername(user.getUsername());
            connectServer();
            System.out.println("Login successful, here is your random long: " + randLong);
        } else {
            if (login != null) {
                login.setText("Login unsuccessful");
            }
            System.out.println("Login unsuccessful");
        }
    }

    public void logout() {
        ui.dispose();
        login = new Login(this);
    }

    public User getUser() {
        return user;
    }

    public Messaging getUI() {
        return ui;
    }

    public SendThread getSendThread() {
        return sendThread;
    }


    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public String getTargetHostName() {
        return targetHostName;
    }

    public void setTargetHostName(String targetHostName) {
        this.targetHostName = targetHostName;
    }

    public void sendMessage(String message) {
        if (sendThread != null) {
            sendThread.sendText(message);
        }
    }
}
