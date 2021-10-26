package uc3m.crypto.client.controller;

import uc3m.crypto.client.view.Welcome;
import uc3m.crypto.security.AES;
import uc3m.crypto.security.DH;
import uc3m.crypto.security.SHA;
import uc3m.crypto.server.model.DB;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;
import uc3m.crypto.client.view.Login;
import uc3m.crypto.client.view.Messaging;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Controller {
    private Random random;
    private Welcome welcome;
    private Messaging ui;
    private volatile User user;
    private String username;
    private SendThread sendThread;
    private ConnectServer connectThread;

    private SecretKey key;
    private IvParameterSpec iv;

    private int targetPort;
    private String targetHostName;

    public Controller() {
        random = new Random();
        welcome = new Welcome(this);

        targetHostName = "localhost";
        targetPort = 5505;
    }

    public static void main(String[] args) {
        Controller controller = new Controller();

        Message msg = new Message("King", "Hello world", new Date());
        Message msg2 = new Message(msg.toString());
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
            while (/*controller.getUser() != null || */!isInterrupted()) {
                try {
                    Socket socket = new Socket(targetHostName, targetPort);
                    DH dh = new DH();
                    byte[] secret = dh.init(socket, false);
                    //System.out.println(secret);
                    key = AES.generateKeyFromSecret(secret);
                    iv = AES.generateIvFromSecret(secret);
                    welcome.changeCard("Login");
                    writeLine("Connected to server.");
                    new ReceiveThread(socket, controller).start();
                    sendThread = new SendThread(socket, controller);
                    break;
                } catch (UnknownHostException e) {
                    writeLine("Unknown Host: " + e.getMessage());
                    System.out.println("Unknown Host: " + e.getMessage());
                } catch (IOException e) {
                    writeLine("I/O Exception: " + e.getMessage());
                    System.out.println("I/O Exception: " + e.getMessage());
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception ex) {
                    writeLine("Sleep error: " + ex.getMessage());
                    break;
                }
            }
        }
    }

    public void writeLine(String text) {
        if (ui != null) {
            ui.writeLine(text);
        }
        else if (welcome != null) {
            welcome.setText(text);
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
        setUsername(username);
        sendMessage(username);
        sendMessage(Base64.getEncoder().encodeToString(SHA.digest(password)));
    }

    public void loginSuccess() {
        welcome.dispose();
        ui = new Messaging(this);
        ui.setUsername(getUsername());
    }

    public void loginFailure() {
        if (welcome != null) {
            welcome.setText("Login unsuccessful");
        }
    }


    public void logout() {
        ui.dispose();
        sendMessage("///LOGGING_OUT");
        welcome = new Welcome(this);
        user = null;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMessage(String message) {
        if (sendThread != null) {
            sendThread.sendText(message);
        }
    }
}
