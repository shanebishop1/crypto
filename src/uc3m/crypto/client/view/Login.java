package uc3m.crypto.client.view;

import uc3m.crypto.client.controller.Controller;

import java.awt.event.*;

import javax.swing.*;


public class Login extends JFrame implements KeyListener {
    private final Controller controller;
    private final JTextArea out;
    private final JTextField username, password;
    private final JLabel usernameLabel, passwordLabel;
    private final JButton login;
    public Login(Controller controller){
        this.controller = controller;
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);

        out = new JTextArea(3, 30);
        out.setLineWrap(true);
        out.setWrapStyleWord(true);
        out.setEditable(false);

        username = new JTextField(25);
        usernameLabel = new JLabel("Username");
        password = new JPasswordField(25);
        password.addKeyListener(this);
        passwordLabel = new JLabel("Password");
        login = new JButton("login");
        login.addActionListener(e -> login());

        usernameLabel.setBounds(50,150,100,30);
        passwordLabel.setBounds(50,220,100,30);
        username.setBounds(150,150,150,30);
        password.setBounds(150,220,150,30);
        //showPassword.setBounds(150,250,150,30);
        login.setBounds(50,300,100,30);
        out.setBounds(50, 370, 250, 60);

        this.add(usernameLabel);
        this.add(username);
        this.add(passwordLabel);
        this.add(password);
        this.add(login);
        this.add(out);

        this.setBounds(10, 10, 370, 600);
        this.setVisible(true);
    }
    public void keyPressed(KeyEvent e) {
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == password) {
            if(e.getKeyChar() == (int)'\n'){
                e.consume();
                login();
            }
        }
    }
    public void login() {
        controller.login(username.getText(), password.getText());
    }

    public void setText(String text) {
        out.setText(text);
    }

    public void clear() {
        username.setText("");
        clearPassword();
        setText("");
    }

    public void clearPassword() {
        password.setText("");
    }
}