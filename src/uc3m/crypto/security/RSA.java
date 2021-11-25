package uc3m.crypto.security;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

public class RSA {
    public static String sign(String data, PrivateKey key) {
        try {
            Signature sig = Signature.getInstance("SHA256WithRSA");
            sig.initSign(key);
            sig.update(data.getBytes());
            byte[] signatureBytes = sig.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean verifySignature(String data, PublicKey key, String signature){

        try {
            Signature sig = Signature.getInstance("SHA256WithRSA");

            sig.initVerify(key);
            sig.update(data.getBytes());
            return sig.verify(Base64.getDecoder().decode(signature));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
