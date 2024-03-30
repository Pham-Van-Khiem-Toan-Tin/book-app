package com.book.app.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Properties;

public class TokenUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "your_secret_key_here";

    public static Key generateKey() throws Exception {
        byte[] keyValue = SECRET_KEY.getBytes();
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

    public static String encrypt(String valueToEnc, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = cipher.doFinal(valueToEnc.getBytes());
        byte[] encryptedByteValue = Base64.getEncoder().encode(encValue);
        System.out.println("Encrypted Value :: " + new String(encryptedByteValue));
        return new String(encryptedByteValue);
    }

    public static String decrypt(String encryptedValue, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue.getBytes());
        byte[] enctVal = cipher.doFinal(decodedBytes);
        System.out.println("Decrypted Value :: " + new String(enctVal));
        return new String(enctVal);
    }

    public static void saveToken(String token) {
        Properties props = new Properties();
        props.setProperty("token", token);
        try (FileOutputStream fos = new FileOutputStream("application.properties")) {
            props.store(fos, "Token information");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readToken() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("application.properties")) {
            props.load(fis);
            return props.getProperty("token");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
