package com.book.app.Utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordUtils {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(password.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hashedPassword);
    }

    // Hàm này kiểm tra mật khẩu đã mã hóa có khớp với mật khẩu ban đầu không
    public static boolean checkPassword(String candidatePassword, String storedPassword) throws NoSuchAlgorithmException {
        String[] parts = storedPassword.split("\\$");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hashedPassword = Base64.getDecoder().decode(parts[1]);

        byte[] candidateHashedPassword = hashPassword(candidatePassword.toCharArray(), salt);
        return constantTimeEquals(candidateHashedPassword, hashedPassword);
    }

    // Hàm này sinh salt
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // Hàm này sử dụng PBE (Password-Based Encryption) để mã hóa mật khẩu
    private static byte[] hashPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new NoSuchAlgorithmException(e);
        }
    }

    // Hàm này kiểm tra hai mảng byte có bằng nhau hay không
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
