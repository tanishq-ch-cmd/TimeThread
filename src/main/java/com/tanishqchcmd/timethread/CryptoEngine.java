package com.tanishqchcmd.timethread;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class CryptoEngine {

    // --- 1. AES (SYMMETRIC) METHODS ---
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static SecretKey getMasterKey() {
        // A 32-character string equals exactly 256 bits for AES
        byte[] key = "TimeThreadSecureBackendKey_2026!".getBytes();
        return new javax.crypto.spec.SecretKeySpec(key, "AES");
    }

    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
    }

    // --- 2. RSA (ASYMMETRIC) METHODS ---
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048); // 2048-bit RSA is the standard for secure handshakes
        return keyPairGen.generateKeyPair();
    }

    public static byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    public static SecretKey decryptAESKeyWithRSA(byte[] encryptedAesKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedAesKey);
        return new javax.crypto.spec.SecretKeySpec(decryptedBytes, "AES");
    }

    // --- 3. TEST THE FULL HYBRID HANDSHAKE ---
    public static void main(String[] args) {
        try {
            System.out.println("--- SVALINN SECURE HANDSHAKE SIMULATION ---");

            // Step 1: Node B generates RSA keys and shares the Public Key
            KeyPair nodeBRSA = generateRSAKeyPair();
            PublicKey nodeBPublicKey = nodeBRSA.getPublic();
            PrivateKey nodeBPrivateKey = nodeBRSA.getPrivate();
            System.out.println("[NODE B] Generated 2048-bit RSA Key Pair.");

            // Step 2: Node A generates the AES Session Key
            SecretKey nodeAAESKey = generateAESKey();
            System.out.println("[NODE A] Generated 256-bit AES Session Key.");

            // Step 3: Node A locks the AES key with Node B's Public Key
            byte[] encryptedAESKey = encryptAESKeyWithRSA(nodeAAESKey, nodeBPublicKey);
            System.out.println("[NODE A] AES Key locked with Node B's RSA Public Key. Transmitting...");

            // Step 4: Node B unlocks the AES key with its Private Key
            SecretKey decryptedAESKey = decryptAESKeyWithRSA(encryptedAESKey, nodeBPrivateKey);
            System.out.println("[NODE B] AES Key successfully unlocked and installed.");

            // Step 5: Prove they both have the same exact key by sending an encrypted message
            System.out.println("\n--- INITIATING SECURE COMMS ---");
            String message = "The grid is fully secured.";
            System.out.println("Node A sends: " + message);

            String cipherText = encrypt(message, nodeAAESKey); // Node A encrypts
            String plainText = decrypt(cipherText, decryptedAESKey); // Node B decrypts

            System.out.println("Node B reads: " + plainText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}