package com.mulesoft.azure.vault.provider.api;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class AESEncryptDecrypt256RandomIvTEST {

	public static byte[] encrypt(String plainText, String key) throws Exception {
		byte[] clean = plainText.getBytes();

		// Generating IV.
		int ivSize = 16;
		byte[] iv = new byte[ivSize];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		// Hashing key.
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(key.getBytes("UTF-8"));
		byte[] keyBytes = new byte[16];
		System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

		// Encrypt.
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] encrypted = cipher.doFinal(clean);

		// Combine IV and encrypted part.
		//byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
		//System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
		//System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);

		return encrypted;
	}

	public static String decrypt(byte[] encryptedIvTextBytes, String key) throws Exception {
		int ivSize = 16;
		int keySize = 16;

		// Extract IV.
		byte[] iv = new byte[ivSize];
		System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

		 //Extract encrypted part.
		int encryptedSize = encryptedIvTextBytes.length - ivSize;
		byte[] encryptedBytes = new byte[encryptedSize];
		System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

		// Hash key.
		byte[] keyBytes = new byte[keySize];
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(key.getBytes());
		System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

		// Decrypt.
		Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

		return new String(decrypted);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		  String key = "G-KaNdRgUkXp2s5v8y/B?E(H+MbQeShV";
	        String clean = "6a4854e2-2d49-44ec-8eb9-963b844e3033";
	        
	        byte[] encrypted;
	        
			try {
				encrypted = AESEncryptDecrypt256RandomIvTEST.encrypt(clean, key);
				//encrypted = "mmbcDOrU2hgVCRBSI3jayNEMzFekbetpDpNjPddUTmPDhx5K3gQEgZ37SIDaPUr6"
				 String decrypted = AESEncryptDecrypt256RandomIvTEST.decrypt(encrypted, key);
				 System.out.println("encrypted >>>> "+encrypted.toString());
				 System.out.println("decrypted >>>> "+decrypted);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
		
	}
}
