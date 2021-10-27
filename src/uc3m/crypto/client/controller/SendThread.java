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

    public SendThread(Socket socket, Controller controller) { //class for sending messages
        this.socket = socket; //contrary to our first belief, it does not have to be a thread but we have kept the name
        this.controller = controller;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true); //write for easy String transmission through the socket

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            controller.getUI().writeLine("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            System.out.println("IO Exception: " + e.getMessage());
//        }
    }

    synchronized public void sendText(String outMsg) { //easy sendTest function, encrypts each string sent
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


