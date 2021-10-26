package uc3m.crypto.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {
    public static byte[] digest(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            return hash;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
