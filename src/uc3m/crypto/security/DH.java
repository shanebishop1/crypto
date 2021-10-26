package uc3m.crypto.security;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class DH {
    public DH() {

    }

    public byte[] init(Socket socket, boolean firstToSend) {
        if (firstToSend) {
            try {
                return initFirstToSend(socket);
            } catch (Exception ex) {
                //System.out.println(ex.getStackTrace());
                ex.printStackTrace();
            }
        } else {
            try {
                return initFirstToReceive(socket);
            } catch (Exception ex) {
                //System.out.println(ex.getStackTrace());
                ex.printStackTrace();
            }
        }

        return null;
    }

    public byte[] initFirstToSend(Socket socket) throws Exception {
        OutputStream output = socket.getOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(output);
        InputStream input = socket.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(input);

        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(2048);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate());

        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();

        /*dout.writeInt(alicePubKeyEnc.length);
        dout.write(alicePubKeyEnc);*/

        oout.writeObject(alicePubKeyEnc);
        //System.out.println("Alice Public Key: " + new String(alicePubKeyEnc, StandardCharsets.US_ASCII) + ", Length: " + alicePubKeyEnc.length);

        /*int length = din.readInt();
        byte[] bobPubKeyEnc = new byte[length];
        din.readFully(bobPubKeyEnc, 0, bobPubKeyEnc.length); // read the message*/
        byte[] bobPubKeyEnc = (byte[]) oin.readObject();

        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
        aliceKeyAgree.doPhase(bobPubKey, true);

        byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();

        //System.out.println("Alice: " + new String(aliceSharedSecret, StandardCharsets.US_ASCII));

        return aliceSharedSecret;
    }

    public byte[] initFirstToReceive(Socket socket) throws Exception {
        OutputStream output = socket.getOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(output);
        InputStream input = socket.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(input);

        /*int length = din.readInt();
        byte[] alicePubKeyEnc = new byte[length];
        din.readFully(alicePubKeyEnc, 0, alicePubKeyEnc.length); // read the message*/

        byte[] alicePubKeyEnc = (byte[]) oin.readObject();

        //System.out.println("Alice Public Key received by Bob: " + new String(alicePubKeyEnc, StandardCharsets.US_ASCII) + ", Length: " + alicePubKeyEnc.length);

        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);

        DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey) alicePubKey).getParams();

        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamFromAlicePubKey);
        KeyPair bobKpair = bobKpairGen.generateKeyPair();

        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKpair.getPrivate());

        byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();

        /*dout.writeInt(bobPubKeyEnc.length);
        dout.write(bobPubKeyEnc);*/
        oout.writeObject(bobPubKeyEnc);

        bobKeyAgree.doPhase(alicePubKey, true);

        byte[] bobSharedSecret = bobKeyAgree.generateSecret();

        //System.out.println("Bob: " + new String(bobSharedSecret, StandardCharsets.US_ASCII));

        return bobSharedSecret;
    }
}
