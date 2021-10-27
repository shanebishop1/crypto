package uc3m.crypto.server.model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) { //Just a data container user class
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
