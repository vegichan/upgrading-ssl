/**
 * Created by martinkysel on 03/11/2015.
 */
import sun.security.x509.*;

import java.io.*;
import java.security.cert.*;
import java.security.*;
import java.math.BigInteger;
import java.util.Date;
import java.security.cert.Certificate;
import java.util.UUID;

public class LTSCertGenerator {

    public static final String outName = "outstore";
    public static final String outFolder = "certs";

    public static final String keyAlias = "myhostkey";
    public static final String defaultPassword = "changeit";
    
    static X509Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm)
            throws GeneralSecurityException, IOException
    {
        PrivateKey privkey = pair.getPrivate();
        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + days * 86400000l);
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(dn);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);

        info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(privkey, algorithm);

        // Update the algorith, and resign.
        algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
        cert = new X509CertImpl(info);
        cert.sign(privkey, algorithm);
        return cert;
    }


    public static void writeP12(KeyPair keyPair, Certificate[] outChain) throws Exception{
        File outFile = new File(outFolder, outName+".p12");
        KeyStore outStore = KeyStore.getInstance("PKCS12");
        outStore.load(null, defaultPassword.toCharArray());
        outStore.setKeyEntry(keyAlias, keyPair.getPrivate(), defaultPassword.toCharArray(), outChain);
        OutputStream outputStream = new FileOutputStream(outFile);
        outStore.store(outputStream, defaultPassword.toCharArray());
        outputStream.flush();
        outputStream.close();
    }

    public static void writeJKS(KeyPair keyPair, X509Certificate cert) throws Exception{
        File outFile = new File(outFolder, outName+".jks");
        KeyStore outStore = KeyStore.getInstance("JKS");
        outStore.load(null, defaultPassword.toCharArray());
        outStore.setCertificateEntry(keyAlias, cert);
        OutputStream outputStream = new FileOutputStream(outFile);
        outStore.store(outputStream, defaultPassword.toCharArray());
        outputStream.flush();
        outputStream.close();
    }

    public static void writePEM(KeyPair keyPair) throws Exception {
        // java is too stupid to do this without Bouncy Castle
    }

    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        UUID randomCN = UUID.randomUUID();

        String CN = String.format("CN=%s, O=%s, L=%s, C=%s", randomCN, "NuoDB", "Cambridge", "US");

        X509Certificate cert = generateCertificate(CN, keyPair, 365, "SHA1withRSA");

        Certificate[] outChain = {cert};

        writeP12(keyPair, outChain);
        writeJKS(keyPair, cert);
        writePEM(keyPair);

    }

}
