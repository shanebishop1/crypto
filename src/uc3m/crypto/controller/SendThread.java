package uc3m.crypto.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private Controller controller;

    public SendThread(Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            writer.println(controller.getUser().getUsername());

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
        writer.println(outMsg);
    }
}


