package uc3m.crypto.view;

import uc3m.crypto.controller.Controller;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class Login extends JFrame implements KeyListener {
    private JTextArea out;
    private JTextField username, password;
    private Controller controller;

    public Login(Controller controller) {
        this.controller = controller;
        this.setLayout(new GridLayout(0, 2));
        out = new JTextArea();
        username = new JTextField(25);
        password = new JPasswordField(25);
        JButton login = new JButton("login");
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.login(username.getText(), password.getText());
            }
        });
        JPanel userInputPanel = new JPanel(new GridLayout(0, 2));
        /*userInputPanel.add(new Label("username:"));
        userInputPanel.add(username);
        userInputPanel.add(new Label("password:"));
        userInputPanel.add(password);
        userInputPanel.setSize(new Dimension(200, 100));*/

        this.add(new Label("username:"));
        this.add(username);
        this.add(new Label("password:"));
        this.add(password);
        this.add(login);
        this.add(out);
        this.setPreferredSize(new Dimension(400, 300));
        this.pack();
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        /*if(e.getKeyChar() == (int)'\n'){
            e.consume();
            process(in.getText());
            in.setText("");
        }*/
    }

    public void setText(String text) {
        out.setText(text);
    }

    public void process(String s) {
        String text = out.getText();
        out.setText(text + ((text.length() == 0) ? "" : "\n") + s);
    }
}