package uc3m.crypto.client.controller;

import uc3m.crypto.client.view.Messaging;
import uc3m.crypto.client.view.Welcome;
import uc3m.crypto.security.AES;
import uc3m.crypto.security.DH;
import uc3m.crypto.security.SHA;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Date;
import java.util.Random;


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

    public void connectServer(boolean maintainLabel) {
        if (connectThread != null) {
            connectThread.interrupt();
        }
        connectThread = new ConnectServer(this, maintainLabel);
        connectThread.start();
    }

    public void writeLine(String text) {
        if (ui != null) {
            ui.writeLine(text);
        } else if (welcome != null) {
            welcome.setText(text);
        }
    }

    public void login(String username, String password) {
        setUsername(username);
        sendMessage("LogMeIn");
        sendMessage(username);
        String hashPass = Base64.getEncoder().encodeToString(SHA.digest(password));
        sendMessage(hashPass);
    }

    public void loginSuccess() {
        welcome.dispose();
        ui = new Messaging(this);
        ui.setUsername(getUsername());
    }

    public void loginFailure() {
        connectServer(true);
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

    public void signUp(String username, String password) {
        setUsername(username);
        sendMessage("SignMeUp");
        sendMessage(username);
        String hashPass = Base64.getEncoder().encodeToString(SHA.digest(password));
        sendMessage(hashPass);
    }

    public void signUpFailure() {
        connectServer(true);
        if (welcome != null) {
            welcome.setText("Sign-up invalid");
        }
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

    class ConnectServer extends Thread {
        private Controller controller;
        private boolean maintainLabel;

        public ConnectServer(Controller controller, boolean maintainLabel) {
            this.controller = controller;
            this.maintainLabel = maintainLabel;
        }

        public void run() {
            try {
                Socket socket = new Socket(targetHostName, targetPort);
                DH dh = new DH();
                byte[] secret = dh.init(socket, false);
                //System.out.println(secret);
                key = AES.generateKeyFromSecret(secret);
                iv = AES.generateIvFromSecret(secret);
                if (!maintainLabel) {
                    welcome.changeCard("Login");
                    writeLine("Connected to server.");
                }
                new ReceiveThread(socket, controller).start();
                sendThread = new SendThread(socket, controller);
                welcome.getUsernameField().requestFocusInWindow();
            } catch (UnknownHostException e) {
                writeLine("Unknown Host: " + e.getMessage());
                System.out.println("Unknown Host: " + e.getMessage());
            } catch (IOException e) {
                writeLine("I/O Exception: " + e.getMessage());
                System.out.println("I/O Exception: " + e.getMessage());
            }
        }
    }
}
