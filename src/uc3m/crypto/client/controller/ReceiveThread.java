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
                if (receivedMessagesCounter == 0) {
                    byte[] decodedKey = Base64.getDecoder().decode(response);
                    System.out.println("Response(Key): " + response);
                    controller.setKey(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"));
                }
                else if (receivedMessagesCounter == 1) {
                    byte[] decodedIv = Base64.getDecoder().decode(response);
                    System.out.println("Response(IV): " + response);
                    controller.setIv(new IvParameterSpec(decodedIv, 0, decodedIv.length));
                }
                else {
                    try {
                        System.out.println("Response: " + response);
                        String plainMsg = AES.decrypt("AES/CBC/PKCS5Padding", response, controller.getKey(), controller.getIv());
                        Message msg = new Message(plainMsg);
                        controller.getUI().writeLine(msg.toUIString());
                    } catch (Exception ex) {
                        System.out.println("Receive Error: " + ex.getMessage());
                    }
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
