package uc3m.crypto.controller;

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
            this.controller.getUI().process("IO Exception: " + e.getMessage());
            System.out.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                controller.getUI().process(response);
            } catch (IOException e) {
                controller.getUI().process("IO Exception: " + e.getMessage());
                System.out.println("IO Exception: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
