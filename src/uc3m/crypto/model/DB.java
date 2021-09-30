package uc3m.crypto.model;

import java.util.ArrayList;
import java.util.List;

public class DB {
    List<User> users = new ArrayList<User>();
    public DB() {
        users.add(new User("Lukas", "Pass1234"));
        users.add(new User("Shane", "Pass0000"));
    }
    public boolean searchUsernamePassword(String username, String password) {
        for (User user : users) {
            if (user != null) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }
}
