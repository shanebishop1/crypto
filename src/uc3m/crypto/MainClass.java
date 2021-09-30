package uc3m.crypto;

import com.sun.tools.javac.Main;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import javax.swing.*;


public class MainClass {
    private static String [][] db;
    private static Random random;
    private static Login login;

    public MainClass(){
        random = new Random();
        db = new String [10][2];
        db[5][0] = "Luky";
        db[5][1] = "Bily";

        login = new Login();
        login.setVisible(true);
        login.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
    }

    public static void login() {
        System.out.print("Username: ");
        Scanner input = new Scanner(System.in);
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();
        login(username, password);
    }

    public static void login(String username, String password ) {
        boolean loggedIn = false;
        for (String[] data : db) {
            if (data[0] != null && data[1] != null) {
                if (data[0].equals(username) && data[1].equals(password)) {
                    loggedIn = true;
                    break;
                }
            }
        }

        long randLong = random.nextLong();

        if (loggedIn) {
            if (login != null) {
                login.setText("Login successful, here is your random long: " + randLong);
            }
            System.out.println("Login successful, here is your random long: " + randLong);
        }
        else {
            if (login != null) {
                login.setText("Login unsuccessful");
            }
            System.out.println("Login unsuccessful");
        }
    }
}
