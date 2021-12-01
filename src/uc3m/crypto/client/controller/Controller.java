package uc3m.crypto.client.controller;

import uc3m.crypto.client.view.Messaging;
import uc3m.crypto.client.view.Welcome;
import uc3m.crypto.security.AES;
import uc3m.crypto.security.DH;
import uc3m.crypto.security.SHA;
import uc3m.crypto.security.X509;
import uc3m.crypto.server.model.Message;
import uc3m.crypto.server.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
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

    private PrivateKey privateKey;

    private boolean isSignedMode;

    public Controller() {
        X509.setPath("C:\\Users\\lukyb\\Documents\\openssl\\");
        X509.setPath("./openssl/");
        random = new Random();
        welcome = new Welcome(this);

        //default settings
        targetHostName = "localhost";
        targetPort = 5505;
        privateKey = null;

        isSignedMode = false;
    }

    public static void main(String[] args) { //Main client function
        Controller controller = new Controller();
    }

    public void connectServer(boolean maintainLabel) { //start a thread for connecting to the server
        if (connectThread != null) {
            connectThread.interrupt();
        }
        connectThread = new ConnectServer(this, maintainLabel);
        connectThread.start();
    }

    public void writeLine(String text) { //forwards writeLine to the active user interface
        if (ui != null) {
            ui.writeLine(text);
        } else if (welcome != null) {
            welcome.setText(text);
        }
    }

    public void login(String username, String password) { //login sequence
        privateKey = X509.loadPrivateKey(username);
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
        ui.setSignedModeCheckboxVisibility(privateKey != null);
    }

    public void loginFailure() {
        connectServer(true);
        if (welcome != null) {
            welcome.setText("Login unsuccessful");
        }
    }

    public void logout() { //logout sequence
        ui.setPrivateMessageReceiver("");
        ui.dispose();
        sendMessage("///LOGGING_OUT");
        welcome = new Welcome(this);
        user = null;

    }

    public void signUp(String username, String password) { //signup sequence
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

    public boolean isSignedMode() {
        return isSignedMode;
    }

    public void setSignedMode(boolean signedMode) {
        isSignedMode = signedMode;
    }

    public void sendMessage(String message) {
        if (sendThread != null) {
            sendThread.sendText(message);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    class ConnectServer extends Thread { //helper class, thread for retrying the connection to the server
        private Controller controller;
        private boolean maintainLabel;

        public ConnectServer(Controller controller, boolean maintainLabel) {
            this.controller = controller;
            this.maintainLabel = maintainLabel;
        }

        public void run() {
            try {
                Socket socket = new Socket(targetHostName, targetPort);
                DH dh = new DH(); //Diffie Hellman
                byte[] secret = dh.init(socket, false); //Server executes this too, the result is a shared secret
                //System.out.println(secret);
                key = AES.generateKeyFromSecret(secret);
                iv = AES.generateIvFromSecret(secret); //key and iv for future AES use
                if (!maintainLabel) { //if the window should change to login
                    welcome.changeCard("Login");
                    writeLine("Connected to server.");
                }
                new ReceiveThread(socket, controller).start(); //Thread for receiving messages
                sendThread = new SendThread(socket, controller); //sending messages, does not have to be a new thread
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
