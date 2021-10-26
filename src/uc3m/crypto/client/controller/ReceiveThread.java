package uc3m.crypto.client.controller;

import uc3m.crypto.security.AES;
import uc3m.crypto.server.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
        while (true) {
            try {
                String response = reader.readLine();
                if (response == null) {
                    break;
                }
                try {
                    String plainMsg = AES.decrypt("AES/CBC/PKCS5Padding", response, controller.getKey(), controller.getIv());
                    Message msg = new Message(plainMsg);
                    if (msg.getSender().equals("Server")) {
                        switch (msg.getContent()) {
                            case "ACCEPTED", "SIGNED UP" -> controller.loginSuccess();
                            case "INVALID SIGNUP" -> controller.signUpFailure();
                            case "DENIED" -> controller.loginFailure();
                        }
                    }
                    if (controller.getUI() != null) controller.getUI().writeLine(msg.toUIString());
                } catch (Exception ex) {
                    System.out.println("Receive Error: " + ex.getMessage());
                }
            } catch (IOException e) {
                controller.getUI().writeLine("IO Exception: " + e.getMessage());
                System.out.println("IO Exception: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
