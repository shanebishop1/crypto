package uc3m.crypto.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA { //SHA helper funcion
    public static byte[] digest(String text) { //wrapper for generating SHA-256 hash
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            return hash;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static String digestToString(String text) {
        return Base64.getEncoder().encodeToString(digest(text));
    }
}
