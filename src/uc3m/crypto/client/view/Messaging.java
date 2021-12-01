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
    private JTextField in, privateMessageReceiver;
    private JLabel usernameLabel;
    private JScrollBar outScrollbar;
    private JPanel panelNorth;
    private JCheckBox signedModeCheckBox, endToEndCheckBox;
    private JButton logoutButton;
    private JPanel Settings;
    private JButton settingsButton;
    private JTextField targetPort;
    private JTextField hostname;
    private JButton applyButton;
    private JButton exitButton;
    private JPanel outWrapper;
    private JScrollPane outScrollable;

    public Messaging(Controller controller) {//User interface for messaging, large part has been generated by intellij
        this.controller = controller;        //form designer, but still highly customised code
        setup();
        setEvents();
    }

    private void setup() {
        Color backgroundColor = new Color(24, 24, 24);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfScreenHeight = (int) (screenSize.height / 2);
        int halfScreenWidth = (int) (screenSize.width / 2);
        int frameWidth = 800;
        int frameHeight = 500; //centering
        this.setBounds(halfScreenWidth - frameWidth / 2, halfScreenHeight - frameHeight / 2, frameWidth, frameHeight);
        Font labelFont14 = new Font(Font.SANS_SERIF, Font.BOLD, 14);

        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainPanel = new JPanel();
        MainPanel.setBackground(backgroundColor);

        MainPanel.setLayout(new CardLayout(0, 0));
        this.add(MainPanel);
        //MESSAGES PANEL
        Messages = new JPanel();
        Messages.setLayout(new BorderLayout(0, 0));
        MainPanel.add(Messages, "Messages");
        outWrapper = new JPanel(new BorderLayout());
        outWrapper.setBackground(new Color(24, 24, 24));
        outScrollable = new JScrollPane(outWrapper, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        outScrollable.getVerticalScrollBar().setUnitIncrement(16);
        out = new JTextArea();
        out.setFont(labelFont14);
        out.setForeground(new Color(255,255,255));
        out.setBackground(backgroundColor);
        out.setEditable(false);
        out.setWrapStyleWord(true);
        out.setLineWrap(true);
        outWrapper.add(out, BorderLayout.CENTER);
        Messages.add(outScrollable, BorderLayout.CENTER);
//        contacts = new JList();
//        contacts.setBackground(new Color(24,24,24));
//        contacts.setPreferredSize(new Dimension(100, 0));
//        Messages.add(contacts, BorderLayout.WEST);
        panelSouth = new JPanel();
        panelSouth.setBackground(backgroundColor);
        panelSouth.setLayout(new BorderLayout(0, 0));
        Messages.add(panelSouth, BorderLayout.SOUTH);
        buttonSend = new JButton();
        buttonSend.setBackground(backgroundColor);
        buttonSend.setFont(labelFont14);
        buttonSend.setOpaque(false);
        buttonSend.setFocusable(false);
        buttonSend.setText("Send");
        panelSouth.add(buttonSend, BorderLayout.EAST);
        in = new JTextField();
        in.setColumns(0);
        in.setMinimumSize(new Dimension(50, 30));
        in.setPreferredSize(new Dimension(50, 30));
        panelSouth.add(in, BorderLayout.CENTER);
        usernameLabel = new JLabel();
        usernameLabel.setForeground(Color.white);
        usernameLabel.setFont(labelFont14);
        usernameLabel.setText("Username:");
        panelSouth.add(usernameLabel, BorderLayout.WEST);
        /*outScrollbar = new JScrollBar();
        Messages.add(outScrollbar, BorderLayout.EAST);*/
        panelNorth = new JPanel();
        panelNorth.setBackground(backgroundColor);
        panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        Messages.add(panelNorth, BorderLayout.NORTH);
        signedModeCheckBox = new JCheckBox();
        signedModeCheckBox.setText("Signed mode");
        signedModeCheckBox.setForeground(Color.white);
        signedModeCheckBox.setFont(labelFont14);
        signedModeCheckBox.setBackground(backgroundColor);
        panelNorth.add(signedModeCheckBox);
        privateMessageReceiver = new JTextField();
        privateMessageReceiver.setColumns(0);
        privateMessageReceiver.setMinimumSize(new Dimension(100, 30));
        privateMessageReceiver.setPreferredSize(new Dimension(100, 30));
        panelNorth.add(privateMessageReceiver);
        logoutButton = new JButton();
        logoutButton.setBackground(backgroundColor);
        logoutButton.setForeground(Color.white);
        logoutButton.setFont(labelFont14);
        logoutButton.setOpaque(false);
        logoutButton.setText("Logout");
        panelNorth.add(logoutButton);
        settingsButton = new JButton();
        settingsButton.setBackground(backgroundColor);
        settingsButton.setFont(labelFont14);
        settingsButton.setOpaque(false);
        settingsButton.setText("Connect");
        panelNorth.add(settingsButton);
        //SETTINGS PANEL
        Settings = new JPanel();
        Settings.setBackground(backgroundColor);
        Settings.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        MainPanel.add(Settings, "Settings");
        final JPanel panel1 = new JPanel();
        panel1.setBackground(backgroundColor);
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        Settings.add(panel1);
        /*final JLabel label1 = new JLabel();
        label1.setText("Listen on port:");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listenPort = new JTextField();
        panel1.add(listenPort, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));*/
        final JLabel label2 = new JLabel();
        label2.setText("Target Port:");
        label2.setForeground(Color.white);
        label2.setFont(labelFont14);
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        targetPort = new JTextField();
        panel1.add(targetPort, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Target IP:");
        label3.setForeground(Color.white);
        label3.setFont(labelFont14);
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hostname = new JTextField();
        panel1.add(hostname, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Apply");
        panel1.add(applyButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setText("Exit");
        panel1.add(exitButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        this.setSize(new Dimension(frameWidth, frameHeight));
    }

    private void setEvents() { //events for buttons
        logoutButton.addActionListener(e -> controller.logout());
        buttonSend.addActionListener(e -> controller.sendMessage(getUserInput()));
        in.addKeyListener(this); //event listener for sending by pressing ENTER
        settingsButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) (MainPanel.getLayout());
            cardLayout.show(MainPanel, "Settings");
            //listenPort.setText(controller.getTargetPort()+"");
            targetPort.setText(controller.getTargetPort() + "");
            hostname.setText(controller.getTargetHostName() + "");
        });
        exitButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) (MainPanel.getLayout());
            cardLayout.show(MainPanel, "Messages");
        });
        applyButton.addActionListener(e -> {
            applySettings();
            CardLayout cardLayout = (CardLayout) (MainPanel.getLayout());
            cardLayout.show(MainPanel, "Messages");
        });
        signedModeCheckBox.addChangeListener(e -> {
            boolean isSelected = ((JCheckBox)e.getSource()).isSelected();
            controller.setSignedMode(isSelected);
        });
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) { //the function for sending with ENTER
        if (e.getSource() == in) {
            if (e.getKeyChar() == (int) '\n') {
                e.consume();
                controller.sendMessage(getUserInput());
            }
        }
    }

    public void applySettings() { //Will be used for future settings, for now the user sets the hostname and port on app start
        /*try {
            controller.setTargetPort(Integer.parseInt(targetPort.getText()));
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        controller.setTargetHostName(hostname.getText());
        controller.connectServer(false);*/
    }

    public void setUsername(String username) {
        usernameLabel.setText(username + ":");
    }

    public void writeLine(String line) { //writes line to the big center text field
        if (line == null)
            return;
        if (line.substring(line.length() - 1).toCharArray()[0] != '\n') {
            line += "\n";
        }
        out.setText(out.getText() + line +'\n');
    }
    public void writeLine(String line, boolean isPrivate) { //writes line to the big center text field
        if (line == null)
            return;
        if (line.substring(line.length() - 1).toCharArray()[0] != '\n') {
            line += "\n";
        }
        out.setText(out.getText() + line +'\n');
    }

    public String getUserInput() { //returns user input, sets it to an empty string
        String text = in.getText();
        in.setText("");
        return text;
    }

    public String getPrivateMessageReceiver() {
        return privateMessageReceiver.getText();
    }

    public void setPrivateMessageReceiver(String text) {
        privateMessageReceiver.setText(text);
    }

    public void scrollDown() { //scroll down the output screen, used when receiving a message
        if (outScrollable.getVerticalScrollBar() == null)
            return;
        JScrollBar vertical = outScrollable.getVerticalScrollBar();
        vertical.setValue( vertical.getMaximum() );
    }

    public void setSignedModeCheckboxVisibility(boolean isVisible) {
        signedModeCheckBox.setVisible(isVisible);
    }
}

