package uc3m.crypto.client.view;

import uc3m.crypto.client.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Welcome extends JFrame implements KeyListener {
    private final Controller controller;
    private final JLabel out;
    private final JTextField username, password, serverHostName, serverPort;
    private final JLabel usernameLabel, passwordLabel, serverHostNameLabel, serverPortLabel;
    private final JButton loginButton, signUpButton, connectButton;
    private final JPanel MainPanel, login, connect;

    public Welcome(Controller controller) { //Welcome screen with 2 cards: Connect and Login
        this.controller = controller;
        MainPanel = new JPanel(new CardLayout(0, 0));
        this.add(MainPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Font labelFont16 = new Font(Font.SANS_SERIF, Font.BOLD, 16);
        Font labelFont14 = new Font(Font.SANS_SERIF, Font.BOLD, 14);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfScreenHeight = screenSize.height / 2;
        int halfScreenWidth = screenSize.width / 2;
        int frameWidth = 310;
        int frameHeight = 250;

        this.setBounds(halfScreenWidth - frameWidth / 2, halfScreenHeight - frameHeight / 2, frameWidth, frameHeight);
        this.setResizable(false);

        out = new JLabel("", SwingConstants.CENTER);
        out.setFont(labelFont14);
        this.add(out, BorderLayout.SOUTH);

        //CONNECT
        connect = new JPanel(null);
        connect.setBackground(new Color(24, 24, 24));


        serverHostNameLabel = new JLabel("LAN Server IP");
        serverHostNameLabel.setForeground(Color.white);
        serverHostNameLabel.setFont(labelFont16);
        serverHostName = new JTextField(25);
        serverHostName.addKeyListener(this);
        serverHostName.setText("localhost");

        serverPortLabel = new JLabel("Server Port");
        serverPortLabel.setForeground(Color.white);
        serverPortLabel.setFont(labelFont16);
        serverPort = new JTextField(25);
        serverPort.addKeyListener(this);
        serverPort.setText("5505");

        connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> connect());

        serverHostNameLabel.setBounds(30, 30, 125, 30);
        serverHostName.setBounds(155, 30, 125, 30);
        serverPortLabel.setBounds(30, 80, 125, 30);
        serverPort.setBounds(155, 80, 125, 30);
        connectButton.setBounds(30, 135, 250, 50);

        connect.add(serverHostNameLabel);
        connect.add(serverHostName);
        connect.add(serverPortLabel);
        connect.add(serverPort);
        connect.add(connectButton);

        //LOGIN or SIGNUP
        login = new JPanel(null);
        login.setBackground(new Color(24, 24, 24));

        usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.white);
        usernameLabel.setFont(labelFont16);
        username = new JTextField(25);
        username.addKeyListener(this);

        passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.white);
        passwordLabel.setFont(labelFont16);
        password = new JPasswordField(25);
        password.addKeyListener(this);

        loginButton = new JButton("Log In");
        loginButton.addActionListener(e -> login());

        signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(e -> signUp());

        usernameLabel.setBounds(30, 30, 125, 30);
        username.setBounds(155, 30, 125, 30);
        passwordLabel.setBounds(30, 80, 125, 30);
        password.setBounds(155, 80, 125, 30);
        //showPassword.setBounds(150,250,150,30);
        loginButton.setBounds(30, 135, 115, 50);
        signUpButton.setBounds(165, 135, 115, 50);

        login.add(usernameLabel);
        login.add(username);
        login.add(passwordLabel);
        login.add(password);
        login.add(loginButton);
        login.add(signUpButton);

        MainPanel.add(connect, "Connect");
        MainPanel.add(login, "Login");

        this.setVisible(true);
        changeCard("Connect");
        serverHostName.requestFocusInWindow();
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) { //key listeners for submitting with ENTER
        if (e.getSource() == password) {
            if (e.getKeyChar() == (int) '\n') {
                e.consume();
                login();
            }
        }
        if (e.getSource() == serverPort) {
            if (e.getKeyChar() == (int) '\n') {
                e.consume();
                connect();
            }
        }
    }

    public void login() {
        controller.login(username.getText(), password.getText());
    }

    public void signUp() {
        controller.signUp(username.getText(), password.getText());
    }

    public void connect() { //wrapper for controller.connectServer, sets the connection parameters
        controller.setTargetHostName(serverHostName.getText());
        try {
            controller.setTargetPort(Integer.parseInt(serverPort.getText()));
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        controller.connectServer(false);
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
        ((CardLayout) (MainPanel.getLayout())).show(MainPanel, card);
    } //for changing view

    public JTextField getUsernameField() {
        return this.username;
    }
}
