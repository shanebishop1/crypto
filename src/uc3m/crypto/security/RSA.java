package uc3m.crypto.security;


import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.Base64;

public class RSA {
    public static String encrypt(String data, Certificate certificate) {
        return encrypt(data, certificate.getPublicKey());
    }
    public static String encrypt(String data, PublicKey publicKey) {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = encryptCipher.doFinal(dataBytes);
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedData, PrivateKey privateKey) {
        try {
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] dataBytes = decryptCipher.doFinal(encryptedDataBytes);
            return new String(dataBytes, StandardCharsets.UTF_8);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String sign(String data, PrivateKey privateKey) {
        try {
            data = SHA.digestToString(data); //hashes the data, in case it has not been done yet
            Signature sig = Signature.getInstance("SHA256WithRSA");
            sig.initSign(privateKey);
            sig.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = sig.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean verifySignature(String data, PublicKey publicKey, String signature){

        try {
            data = SHA.digestToString(data);
            Signature sig = Signature.getInstance("SHA256WithRSA");

            sig.initVerify(publicKey);
            sig.update(data.getBytes(StandardCharsets.UTF_8));
            return sig.verify(Base64.getDecoder().decode(signature));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
