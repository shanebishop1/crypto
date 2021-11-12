package uc3m.crypto.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AES { //AES helper class
    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);//the cipher is ready
        byte[] cipherText = cipher.doFinal(input.getBytes()); //the encryption itself
        return Base64.getEncoder()
                .encodeToString(cipherText); //change bytes to a Base64 String
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText); //change bytes to a String
    }

    //generates random key, not used because we have to generate two same keys, so we have to generate it from a (secret) seed
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    //key generation from a secret
    public static SecretKey generateKeyFromSecret(byte[] secret) {
        //len: 32 for AES-256 and len: 16 for AES-128
        SecretKeySpec key = new SecretKeySpec(secret, 0, 32, "AES");
        return key;
    }

    //same as for key, we want to generate it from a secret, so not used
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    //generates the Initialization Vector from a secret using
    // the SHA-256 algorithm and getting the first 16 bytes for a 128-bit AES key
    public static IvParameterSpec generateIvFromSecret(byte[] secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] iv = digest.digest(secret);
            iv = Arrays.copyOfRange(iv, 0, 16);
            return new IvParameterSpec(iv);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

}
