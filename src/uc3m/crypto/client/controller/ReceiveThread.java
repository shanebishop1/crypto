package uc3m.crypto.client.controller;

import uc3m.crypto.security.AES;
import uc3m.crypto.server.model.Message;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Base64;

public class ReceiveThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private Controller controller;

    public ReceiveThread(Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException e) {
            this.controller.getUI().writeLine("IO Exception: " + e.getMessage());
            System.out.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        int receivedMessagesCounter = 0;
        while (true) {
            try {
                String response = reader.readLine();
                if (response == null) {
                    break;
                }
                try {
                    System.out.println("Response: " + response);
                    String plainMsg = AES.decrypt("AES/CBC/PKCS5Padding", response, controller.getKey(), controller.getIv());
                    Message msg = new Message(plainMsg);
                    if (receivedMessagesCounter == 0) {
                        if (msg.getContent().equals("ACCEPTED")) {
                            controller.loginSuccess();
                        }
                        else {
                            controller.loginFailure();
                        }
                    }
                    controller.getUI().writeLine(msg.toUIString());
                } catch (Exception ex) {
                    System.out.println("Receive Error: " + ex.getMessage());
                }
                receivedMessagesCounter++;
            } catch (IOException e) {
                controller.getUI().writeLine("IO Exception: " + e.getMessage());
                System.out.println("IO Exception: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
