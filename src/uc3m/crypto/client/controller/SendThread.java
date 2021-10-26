package uc3m.crypto.client.controller;


import uc3m.crypto.security.AES;
import uc3m.crypto.server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private Controller controller;
    private Server server;

    public SendThread(Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            controller.getUI().writeLine("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //TODO: Close behavior --> close client sockets.
    public void run() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            System.out.println("IO Exception: " + e.getMessage());
//        }
    }

    synchronized public void sendText(String outMsg) {
        if (outMsg != null && !outMsg.equals("") && !outMsg.isBlank()) {
            try {
                String encMsg = AES.encrypt("AES/CBC/PKCS5Padding", outMsg, controller.getKey(), controller.getIv());
                writer.println(encMsg);
            } catch (Exception ex) {
                System.out.println("AES: " + ex.getMessage());
            }
        }
    }
}


