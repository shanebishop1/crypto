package uc3m.crypto.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PBKDF2 {
    //hash password with PBKDF2
    public static byte[] hashPassword( final String password, final String salt, final int iterations, final int keyLength ) {
        final char[] passwordChars = password.toCharArray();
        final byte[] saltBytes = salt.getBytes();
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, keyLength);
            SecretKey key = secretKeyFactory.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException(e);
        }
    }

    //default hash used by our database, 10000 iterations, 512-bit key length
    public static String defaultHash(final String password, final String salt) {
        byte[] hashedBytes = hashPassword(password, salt, 10000, 512);
        String hashedPassword = Base64.getEncoder().encodeToString(hashedBytes);
        return hashedPassword;
    }

    //16 byte salt generator
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
