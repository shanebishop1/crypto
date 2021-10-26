package uc3m.crypto.client.view;

import uc3m.crypto.client.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Messaging extends JFrame implements KeyListener {
    private Controller controller;

    private JPanel MainPanel;
    private JPanel Messages;
    private JTextArea out;
    private JList contacts;
    private JPanel panelSouth;
    private JButton buttonSend;
    private JTextField in;
    private JLabel usernameLabel;
    private JScrollBar outScrollbar;
    private JPanel panelNorth;
    private JCheckBox signedModeCheckBox;
    private JButton logoutButton;
    private JPanel Settings;
    private JButton settingsButton;
    private JTextField listenPort;
    private JTextField targetPort;
    private JTextField hostname;
    private JButton applyButton;
    private JButton exitButton;
    private JButton testButton;

    public Messaging(Controller controller) {
        this.controller = controller;
        setup();
        setEvents();
    }

    private void setup() {
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainPanel = new JPanel();
        MainPanel.setLayout(new CardLayout(0, 0));
        this.add(MainPanel);
        //MESSAGES PANEL
        Messages = new JPanel();
        Messages.setLayout(new BorderLayout(0, 0));
        MainPanel.add(Messages, "Messages");
        out = new JTextArea();
        out.setEditable(false);
        Messages.add(out, BorderLayout.CENTER);
        contacts = new JList();
        contacts.setPreferredSize(new Dimension(100, 0));
        Messages.add(contacts, BorderLayout.WEST);
        panelSouth = new JPanel();
        panelSouth.setLayout(new BorderLayout(0, 0));
        Messages.add(panelSouth, BorderLayout.SOUTH);
        buttonSend = new JButton();
        buttonSend.setFocusable(false);
        buttonSend.setText("Send");
        panelSouth.add(buttonSend, BorderLayout.EAST);
        in = new JTextField();
        in.setColumns(0);
        in.setMinimumSize(new Dimension(50, 30));
        in.setPreferredSize(new Dimension(50, 30));
        panelSouth.add(in, BorderLayout.CENTER);
        usernameLabel = new JLabel();
        usernameLabel.setText("Username:");
        panelSouth.add(usernameLabel, BorderLayout.WEST);
        outScrollbar = new JScrollBar();
        Messages.add(outScrollbar, BorderLayout.EAST);
        panelNorth = new JPanel();
        panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        Messages.add(panelNorth, BorderLayout.NORTH);
        signedModeCheckBox = new JCheckBox();
        signedModeCheckBox.setText("Signed mode");
        panelNorth.add(signedModeCheckBox);
        logoutButton = new JButton();
        logoutButton.setText("Logout");
        panelNorth.add(logoutButton);
        settingsButton = new JButton();
        settingsButton.setText("Connect");
        panelNorth.add(settingsButton);
        testButton = new JButton();
        testButton.setText("Test");
        panelNorth.add(testButton);
        //SETTINGS PANEL
        Settings = new JPanel();
        Settings.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        MainPanel.add(Settings, "Settings");
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        Settings.add(panel1);
        /*final JLabel label1 = new JLabel();
        label1.setText("Listen on port:");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listenPort = new JTextField();
        panel1.add(listenPort, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));*/
        final JLabel label2 = new JLabel();
        label2.setText("Target port:");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        targetPort = new JTextField();
        panel1.add(targetPort, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Target IP:");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hostname = new JTextField();
        panel1.add(hostname, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Apply");
        panel1.add(applyButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Exit");
        panel1.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        this.setSize(new Dimension(800, 500));
    }

    private void setEvents() {
        logoutButton.addActionListener( e -> controller.logout());
        buttonSend.addActionListener(e -> controller.sendMessage(getUserInput()));
        in.addKeyListener(this);
        settingsButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout)(MainPanel.getLayout());
            cardLayout.show(MainPanel, "Settings");
            //listenPort.setText(controller.getTargetPort()+"");
            targetPort.setText(controller.getTargetPort()+"");
            hostname.setText(controller.getTargetHostName()+"");
        });
        exitButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout)(MainPanel.getLayout());
            cardLayout.show(MainPanel, "Messages");
        });
        applyButton.addActionListener(e -> {
            applySettings();
            CardLayout cardLayout = (CardLayout)(MainPanel.getLayout());
            cardLayout.show(MainPanel, "Messages");
        });
        //testButton.addActionListener(e -> controller.dumpMessages());
    }

    public void keyPressed(KeyEvent e) {
    }
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == in) {
            if(e.getKeyChar() == (int)'\n'){
                e.consume();
                controller.sendMessage(getUserInput());
            }
        }
    }

    public void applySettings() {
        try {
            controller.setTargetPort(Integer.parseInt(targetPort.getText()));
        } catch(NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        controller.setTargetHostName(hostname.getText());
        controller.connectServer();
    }

    public void setUsername(String username) {
        usernameLabel.setText(username + ":");
    }

    public void writeLine(String line) {
        if (line == null)
            return;
        if (line.substring(line.length() - 1).toCharArray()[0] != '\n') {
            line += "\n";
        }
        out.setText(out.getText() + line);
    }

    public String getUserInput() {
        String text = in.getText();
        System.out.println(text);
        in.setText("");
        return text;
    }
}

