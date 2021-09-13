package uc3m.crypto;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        String [][] db = new String [10][2];
        db[5][0] = "Luky";
        db[5][1] = "Bil";
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

        if (loggedIn) {
            System.out.println("Login successful");
        }
        else {
            System.out.println("Login unsuccessful");
        }
    }
}
