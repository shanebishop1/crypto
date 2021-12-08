package uc3m.crypto.client.controller;


import uc3m.crypto.security.AES;
import uc3m.crypto.security.X509;
import uc3m.crypto.server.Server;
import uc3m.crypto.server.model.Message;

import java.io.*;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import static uc3m.crypto.server.model.Message.SignatureStatus.*;

public class SendThread extends Thread {
    private PrintWriter writer;
    private final Socket socket;
    private final Controller controller;
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

    synchronized public void sendText(String outMsg) { //easy sendText function, encrypts each string sent
        if (outMsg != null && !outMsg.strip().equals("") && !outMsg.isBlank()) {
            try {
                Message message = new Message(controller.getUsername(), outMsg, new Date());
                String receiver = "";
                boolean isEndToEnd = false;
                String plainContent = message.getContent();
                if (controller.getUI() != null) {
                    receiver = controller.getUI().getPrivateMessageReceiver().strip();
                    if (!receiver.equals("")) {
                        Certificate receiverCert = X509.getUserCertificate(receiver);
                        if (receiverCert != null) {
                            message.setReceiver(receiver);
                            message.encrypt(receiverCert);
                            isEndToEnd = true;
                        }
                        else {
                            controller.getUI().writeLine("The chosen user cannot receive private " +
                                    "messages because their certificate is not available.");
                            return;
                        }
                    }
                }
                message.setHmac(controller.getKey());
                if (controller.getPrivateKey() != null && controller.isSignedMode()) {
                    message.sign(controller.getPrivateKey());
                }
                String encMsg = AES.encrypt("AES/CBC/PKCS5Padding",
                        message.toString(),
                        controller.getKey(),
                        controller.getIv());
                writer.println(encMsg);
                if (isEndToEnd) {
                    Message.SignatureStatus signatureStatus = message.checkSignature();
                    message.setContent(plainContent);
                    controller.getUI().writeLine(message.toUIString(signatureStatus));
                    controller.getUI().scrollDown();
                }
            } catch (Exception ex) {
                System.out.println("AES: " + ex.getMessage());
            }
        }
    }
}


