package uc3m.crypto;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class UI extends JFrame implements KeyListener{
    private JTextArea out;
    private JTextField in;
    public UI(){
        out = new JTextArea();
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
        if(e.getKeyChar() == (int)'\n'){
            e.consume();
            process(in.getText());
            in.setText("");
        }
    }

    public void process(String s){
        String text = out.getText();
        out.setText(text + ((text.length() == 0) ? "" : "\n") + s);
    }

    public static void main(String[] args){
        UI x = new UI();
        x.setVisible(true);
        x.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}