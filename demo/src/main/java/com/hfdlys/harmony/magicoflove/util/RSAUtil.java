package com.hfdlys.harmony.magicoflove.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.ibatis.io.Resources;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RSAUtil {
    private RSAUtil() {
    }

    static RSAUtil instance;

    public static RSAUtil getInstance() {
        if (instance == null) {
            instance = new RSAUtil();
        }
        return instance;
    }

    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * 读取公钥和私钥
     * @param publicKeyFile 公钥文件
     * @param privateKeyFile 私钥文件
     * @throws Exception
     */
    private void readKeyPair(String publicKeyFile, String privateKeyFile) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // 读取公钥
        byte[] publicKeyBytes = readKeyFromFile(publicKeyFile);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // 读取私钥
        byte[] privateKeyBytes = readKeyFromFile(privateKeyFile);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    private byte[] readKeyFromFile(String keyFile) throws IOException {
        try (InputStream fis = Resources.getResourceAsStream(keyFile)) {
            byte[] keyBytes = new byte[fis.available()];
            fis.read(keyBytes);
            return Base64.getDecoder().decode(keyBytes);
        }
    }


    public void init() {
        try {
            readKeyPair("RSAKey/public.key", "RSAKey/private.key");
        } catch (Exception e) {
            log.error("Failed to read RSA key pair", e);
        }
    }


    /**
     * 加密方法
     * @param data
     * @return
     */
    public String encrypt(String data) {
        try {
            if (publicKey == null) {
                init();
            }
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            return data;
        }
    }

    /**
     * 解密方法
     * @param encryptedData
     * @return
     */
    public String decrypt(String encryptedData) {
        try {
            if (privateKey == null) {
                init();
            }
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            return encryptedData;
        }
    }
}