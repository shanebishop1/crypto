package uc3m.crypto.controller;

import uc3m.crypto.model.DB;
import uc3m.crypto.view.Login;

import java.util.Scanner;
import java.util.Random;
import javax.swing.WindowConstants;


public class Controller {
    private static Random random;
    private static Login login;
    private static DB db;

    public Controller(){
        random = new Random();
        db = new DB();
        login = new Login();
        login.setVisible(true);
        login.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
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
        boolean loggedIn = db.searchUsernamePassword(username, password);
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
