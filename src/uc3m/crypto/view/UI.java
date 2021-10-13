package uc3m.crypto.view;

import uc3m.crypto.controller.Controller;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class UI extends JFrame implements KeyListener {
    private JTextArea out;
    private JTextField in;
    private Controller controller;

    public UI(Controller controller) {
        this.controller = controller;
        //TODO: Make scrollable
        out = new JTextArea();
        out.setEditable(false);
        in = new JTextField();
        this.add(out, BorderLayout.CENTER);
        this.add(in, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(700, 500));
        this.pack();
        in.addKeyListener(this);
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == (int) '\n') {
            e.consume();
            String msgString = in.getText();
            controller.getSendThread().sendText(msgString);
            process("[" + controller.getUser().getUsername() + "]: " + msgString);
            in.setText("");
        }
    }

    public void process(String s) {
        String text = out.getText();
        out.setText(text + ((text.length() == 0) ? "" : "\n") + s);
    }

    public static void main(String[] args) {
//        UI x = new UI();
//        x.setVisible(true);
//        x.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}