package com.hfdlys.harmony.magicoflove;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyPairGenerator {
    public static void generateAndSaveKeyPair(String publicKeyFile, String privateKeyFile) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // 保存公钥
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write(Base64.getEncoder().encode(publicKey.getEncoded()));
        }

        // 保存私钥
        try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
            fos.write(Base64.getEncoder().encode(privateKey.getEncoded()));
        }
    }

    public static void main(String[] args) {
        try {
            generateAndSaveKeyPair("public.key", "private.key");
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }
}
