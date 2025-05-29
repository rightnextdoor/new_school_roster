//package com.school.roster.school_roster_backend.utils;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//@Component
//public class CryptoUtils {
//    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
//    private final SecretKeySpec keySpec;
//    private final SecureRandom random = new SecureRandom();
//
//    public CryptoUtils(@Value("${gov.id.secret.hex}") String secretHex) {
//        byte[] keyBytes = hexToBytes(secretHex);
//        if (keyBytes.length != 32) {
//            throw new IllegalArgumentException("Expected 32-byte key, got " + keyBytes.length);
//        }
//        this.keySpec = new SecretKeySpec(keyBytes, "AES");
//    }
//
//    /** Encrypts plaintext → Base64(IV‖ciphertext) */
//    public String encrypt(String plainText) {
//        try {
//            byte[] ivBytes = new byte[16];
//            random.nextBytes(ivBytes);
//            IvParameterSpec iv = new IvParameterSpec(ivBytes);
//
//            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
//            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
//
//            byte[] combined = new byte[ivBytes.length + cipherBytes.length];
//            System.arraycopy(ivBytes,     0, combined, 0,           ivBytes.length);
//            System.arraycopy(cipherBytes, 0, combined, ivBytes.length, cipherBytes.length);
//
//            return Base64.getEncoder().encodeToString(combined);
//        } catch (Exception e) {
//            throw new RuntimeException("Error encrypting data", e);
//        }
//    }
//
//    /** Decrypts Base64(IV‖ciphertext) → plaintext */
//    public String decrypt(String blob) {
//        try {
//            byte[] combined = Base64.getDecoder().decode(blob);
//
//            byte[] ivBytes     = new byte[16];
//            byte[] cipherBytes = new byte[combined.length - 16];
//            System.arraycopy(combined, 0,           ivBytes,     0, 16);
//            System.arraycopy(combined, 16, combined, 0, cipherBytes.length);
//
//            IvParameterSpec iv = new IvParameterSpec(ivBytes);
//            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
//
//            byte[] plainBytes = cipher.doFinal(cipherBytes);
//            return new String(plainBytes, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            throw new RuntimeException("Error decrypting data", e);
//        }
//    }
//
//    /** Simple check: valid Base64 blob longer than IV means “encrypted.” */
//    public boolean isEncrypted(String s) {
//        if (s == null || s.isEmpty()) return false;
//        try {
//            byte[] d = Base64.getDecoder().decode(s);
//            return d.length > 16;
//        } catch (IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    private static byte[] hexToBytes(String hex) {
//        int len = hex.length();
//        if (len % 2 != 0) throw new IllegalArgumentException("Hex string length must be even");
//        byte[] out = new byte[len/2];
//        for (int i = 0; i < len; i += 2) {
//            out[i/2] = (byte)((Character.digit(hex.charAt(i),16) << 4)
//                    + Character.digit(hex.charAt(i+1),16));
//        }
//        return out;
//    }
//}