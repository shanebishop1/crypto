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

public class DH { //Diffie Hellman helper class
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

    public byte[] initFirstToSend(Socket socket) throws Exception { //Diffie Hellman through a socket
        OutputStream output = socket.getOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(output);
        InputStream input = socket.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(input); //Input, Output streams

        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(2048); //initialize DH with 2048 keysize
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair(); //Alice's key pair

        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate()); //alices key agreement, initialized with her privateKey, will be used later

        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();

        oout.writeObject(alicePubKeyEnc); //Alice sends her public key to Bob

        //System.out.println("Alice Public Key: " + new String(alicePubKeyEnc, StandardCharsets.US_ASCII) + ", Length: " + alicePubKeyEnc.length);

        byte[] bobPubKeyEnc = (byte[]) oin.readObject(); //alice receives Bob's public key as encoded bytes

        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc); //Bob's key is created with the same p and q parameters
        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec); //decode Bob's public key
        aliceKeyAgree.doPhase(bobPubKey, true); //perform the power and modulo operations of Diffie Hellman

        byte[] aliceSharedSecret = aliceKeyAgree.generateSecret(); //the secret is generated :)

        //System.out.println("Alice: " + new String(aliceSharedSecret, StandardCharsets.US_ASCII));

        return aliceSharedSecret;
    }

    public byte[] initFirstToReceive(Socket socket) throws Exception {
        OutputStream output = socket.getOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(output);
        InputStream input = socket.getInputStream();
        ObjectInputStream oin = new ObjectInputStream(input);

        byte[] alicePubKeyEnc = (byte[]) oin.readObject();

        //System.out.println("Alice Public Key received by Bob: " + new String(alicePubKeyEnc, StandardCharsets.US_ASCII) + ", Length: " + alicePubKeyEnc.length);

        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec); //regenerates Alice's public key

        DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey) alicePubKey).getParams(); //gets the p and q params from Alice

        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamFromAlicePubKey);
        KeyPair bobKpair = bobKpairGen.generateKeyPair();//generates his own keys, but it's mathematically connected to Alice's

        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKpair.getPrivate());

        byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();

        oout.writeObject(bobPubKeyEnc);

        bobKeyAgree.doPhase(alicePubKey, true);

        byte[] bobSharedSecret = bobKeyAgree.generateSecret(); //secret generated

        //System.out.println("Bob: " + new String(bobSharedSecret, StandardCharsets.US_ASCII));

        return bobSharedSecret;
    }
}
