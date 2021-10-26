package uc3m.crypto.client.view;

import uc3m.crypto.client.controller.Controller;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


public class Welcome extends JFrame implements KeyListener {
    private final Controller controller;
    private final JTextArea out;
    private final JTextField username, password, serverHostName, serverPort;
    private final JLabel usernameLabel, passwordLabel, serverHostNameLabel, serverPortLabel;
    private final JButton loginButton, connectButton;
    private final JPanel MainPanel, login, signup, connect;
    public Welcome(Controller controller){
        this.controller = controller;
        MainPanel = new JPanel(new CardLayout(0, 0));
        this.add(MainPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //CONNECT
        connect = new JPanel(null);

        out = new JTextArea(3, 30);
        out.setLineWrap(true);
        out.setWrapStyleWord(true);
        out.setEditable(false);

        serverHostName = new JTextField(25);
        serverHostNameLabel = new JLabel("Server Host Name");
        serverPort = new JTextField(25);
        serverPort.addKeyListener(this);
        serverPortLabel = new JLabel("Server Port");
        connectButton = new JButton("connect");
        connectButton.addActionListener(e -> connect());

        serverHostNameLabel.setBounds(50,150,100,30);
        serverPortLabel.setBounds(50,220,100,30);
        serverHostName.setBounds(150,150,150,30);
        serverPort.setBounds(150,220,150,30);
        connectButton.setBounds(50,300,100,30);
        out.setBounds(50, 370, 250, 60);

        this.add(out, BorderLayout.SOUTH);

        connect.add(serverHostNameLabel);
        connect.add(serverHostName);
        connect.add(serverPortLabel);
        connect.add(serverPort);
        connect.add(connectButton);
        //LOGIN
        login = new JPanel(null);

        username = new JTextField(25);
        usernameLabel = new JLabel("Username");
        password = new JPasswordField(25);
        password.addKeyListener(this);
        passwordLabel = new JLabel("Password");
        loginButton = new JButton("login");
        loginButton.addActionListener(e -> login());

        usernameLabel.setBounds(50,150,100,30);
        passwordLabel.setBounds(50,220,100,30);
        username.setBounds(150,150,150,30);
        password.setBounds(150,220,150,30);
        //showPassword.setBounds(150,250,150,30);
        loginButton.setBounds(50,300,100,30);

        login.add(usernameLabel);
        login.add(username);
        login.add(passwordLabel);
        login.add(password);
        login.add(loginButton);

        signup = new JPanel(null);

        MainPanel.add(connect, "Connect");
        MainPanel.add(login, "Login");
        this.setBounds(10, 10, 370, 600);
        this.setVisible(true);

        changeCard("Connect");
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
        if (e.getSource() == serverPort) {
            if(e.getKeyChar() == (int)'\n'){
                e.consume();
                connect();
            }
        }
    }
    public void login() {
        controller.login(username.getText(), password.getText());
    }

    public void connect() {
        controller.setTargetHostName(serverHostName.getText());
        try {
            controller.setTargetPort(Integer.parseInt(serverPort.getText()));
        } catch(NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        controller.connectServer();
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

    public void changeCard(String card) {
        ((CardLayout)(MainPanel.getLayout())).show(MainPanel, card);
    }
}
