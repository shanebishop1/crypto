package uc3m.crypto.server.model;

import java.util.ArrayList;
import java.util.List;

public class DB {
    List<User> users = new ArrayList<User>();

    public DB() {
        users.add(new User("Lukas", "p"));
        users.add(new User("Shane", "p"));
    }

    public User searchUsernamePassword(String username, String password) {
        for (User user : users) {
            if (user != null) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;
    }
}
