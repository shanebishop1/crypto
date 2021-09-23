package uc3m.crypto;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;
import java.util.Random;
import java.net.*;

public class MainClass {
    public static void main(String[] args) {

        Random random = new Random();

        String [][] db = new String [10][2];
        db[5][0] = "Luky";
        db[5][1] = "Bily";
        System.out.print("Username: ");
        Scanner input = new Scanner(System.in);
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();
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
            System.out.println("Login successful, here is your random long: " + randLong);
        }
        else {
            System.out.println("Login unsuccessful");
        }

        while(true) {
            try{System.in.read();}
            catch(Exception e){}
        }
    }

    private static char readInput() {
        try {
            Reader reader = new InputStreamReader(System.in);
        }
        catch (Exception e) {
            System.out.println("Error reading from user");
        }
        return 'i';
    }
}
