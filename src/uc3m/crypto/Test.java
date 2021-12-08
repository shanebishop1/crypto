package uc3m.crypto;

import uc3m.crypto.security.AES;
import uc3m.crypto.security.PBKDF2;
import uc3m.crypto.security.SHA;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Test {
    public static void PBKDF2Test() {
        int testNumber = 500;
        int iterations = 100000;
        int keySize = 512;
        short[] timesElapsed = new short[testNumber];

        for (int i = 0; i < testNumber; i++) {
            byte[] randomBytes = new byte[8];
            new SecureRandom().nextBytes(randomBytes);
            String password = SHA.digestToString(Base64.getEncoder().encodeToString(randomBytes)).substring(0, 12);
            byte[] digest = SHA.digest(Base64.getEncoder().encodeToString(randomBytes));
            String salt = Base64.getEncoder().encodeToString(Arrays.copyOfRange(digest, 0, 16));
            long start = System.currentTimeMillis();
            PBKDF2.hashPassword(password, salt, iterations, keySize);
            long finish = System.currentTimeMillis();
            timesElapsed[i] = (short)(finish - start);
        }
        long sum = 0;
        for (int i = 0; i < testNumber; i++) {
            sum += timesElapsed[i];
        }
        sum /= testNumber;
        //generated keyLength does NOT make difference, the internal HMAC keyLength makes a large difference
        System.out.println("Average run time of of PBKDF2 with " +
                iterations + " iterations and " + keySize + "-bit key length: " + sum + "ms");
    }

    public static void AESTest() {
        int testNumber = 1000;
        int keySize = 256;
        int dataSize = 2000000;
        short[] timesElapsed = new short[testNumber];
        for (int i = 0; i < testNumber; i++) {
            byte[] randomBytes = new byte[dataSize];
            new SecureRandom().nextBytes(randomBytes);
            String text = new String(randomBytes);
            try {
                SecretKey key = AES.generateKey(keySize);
                IvParameterSpec iv = AES.generateIv();
                long start = System.nanoTime();
                AES.encrypt("AES/CBC/PKCS5Padding", text, key, iv);
                long finish = System.nanoTime();
                timesElapsed[i] = (short)(finish - start);
            } catch (Exception ex) {System.out.println(ex.getMessage());}
        }
        long sum = 0;
        for (int i = 0; i < testNumber; i++) {
            sum += timesElapsed[i];
        }
        sum /= testNumber;
        System.out.println("Average run time of of AES with "
                + keySize + "-bit key length and data size of " + dataSize + " bytes: " + sum + "ns");
    }


    public static void main(String[] args) {
        //PBKDF2Test();
        AESTest();
    }
}
