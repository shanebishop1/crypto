package uc3m.crypto.security;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.security.cert.CertificateFactory;

public class X509 {
    private static String path;

    public static void setPath(String path) {
        X509.path = path;
    }

    public static X509Certificate generateCertificate(
            X500Name issuer,
            X500Name subject,
            PublicKey subjectPublicKey,
            PrivateKey issuerPrivateKey
    )
            throws CertificateException, OperatorCreationException
    {
        X500Name x500Name = new X500Name("CN=***.com, OU=Security&Defense, O=*** Crypto., L=Ottawa, ST=Ontario, C=CA");
        SubjectPublicKeyInfo pubKeyInfo = SubjectPublicKeyInfo.getInstance(subjectPublicKey.getEncoded());
        final Date start = new Date();
        final Date until = Date.from(LocalDate.now().plus(365, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.UTC));
        final X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(issuer,
                new BigInteger(16, new SecureRandom()), start, until, /*Subject: */ subject, pubKeyInfo
        );
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(issuerPrivateKey);

        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(
                new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));

        System.out.println("x.509 certificate has been successfully generated!");

        return certificate;
    }

    public static X509Certificate getCACertificate(String name) {
        name = name.toLowerCase(Locale.ROOT);
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream is = new FileInputStream (path+name+".pem");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
            is.close();
            return cer;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static X509Certificate getUserCertificate(String username) {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream fin = new FileInputStream (path+username+".crt");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(fin);
            fin.close();
            return cer;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean validateCertificate(X509Certificate certificate) {
        return validateCertificate(certificate, 0);
    }

    public static boolean validateCertificate(X509Certificate certificate, int recursionDepth) {
        try {
            if (certificate == null) {
                System.out.println("Error: Certificate not found");
                return false;
            }
            try {
                certificate.checkValidity();
            }
            catch (CertificateException ex) {
                ex.printStackTrace();
                return false;
            }
            X500Name subject = new JcaX509CertificateHolder(certificate).getSubject();
            RDN cnSubject = subject.getRDNs(BCStyle.CN)[0];
            String cnSubjectString = IETFUtils.valueToString(cnSubject.getFirst().getValue());
            X500Name issuer = new JcaX509CertificateHolder(certificate).getIssuer();
            RDN cnIssuer = issuer.getRDNs(BCStyle.CN)[0];
            String cnIssuerString = IETFUtils.valueToString(cnIssuer.getFirst().getValue());
            X509Certificate caCert = getCACertificate(cnIssuerString);
            certificate.verify(caCert.getPublicKey());
            System.out.println(cnSubjectString + " is valid.");
            if (cnIssuerString.equals("RCA") && cnIssuerString.equals(cnSubjectString)) {
                return true;
            }
            else {
                if (recursionDepth > 10) {
                    return false;
                }
                return validateCertificate(caCert, ++recursionDepth);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static PrivateKey loadPrivateKey(String username) {
        try {
            Path keyPath = Paths.get(path+username+"/"+username+".key.pem");
            String keyFileString = Files.readString(keyPath, StandardCharsets.US_ASCII);

            String privateKeyPem = keyFileString.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
            privateKeyPem = privateKeyPem.replace("-----END RSA PRIVATE KEY-----", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        X509.setPath("C:\\Users\\lukyb\\Documents\\openssl\\");
        System.out.println(validateCertificate(getUserCertificate("lukas")));
    }
}
