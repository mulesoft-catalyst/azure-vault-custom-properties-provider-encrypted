 package com.mulesoft.azure.vault.provider.api;

 import com.mulesoft.azure.vault.provider.api.exception.MuleEncryptionException;
 import com.mulesoft.azure.vault.provider.api.jce.EncrypterBuilder;
 import com.mulesoft.azure.vault.provider.api.jce.EncryptionAlgorithm;
 import com.mulesoft.azure.vault.provider.api.jce.JCEEncrypter;
 import org.bouncycastle.util.encoders.Base64;

 public class CustomCodeSecretProperties{
	

	public static String decrypt(String encryptString, String decodedKey) throws MuleEncryptionException {

		JCEEncrypter encrypter = EncryptionAlgorithm.valueOf("AES").getBuilder().forKey(decodedKey).using(EncrypterBuilder.EncryptionMode.CBC).useRandomIVs(true).build();

		String decrryptedString = new String(encrypter.decrypt(Base64.decode(encryptString)));
		return decrryptedString;
	}

	 public static String encrypt(String plaintext, String decodedKey) throws MuleEncryptionException {

		 JCEEncrypter encrypter = EncryptionAlgorithm.valueOf("AES").getBuilder().forKey(decodedKey).using(EncrypterBuilder.EncryptionMode.CBC).useRandomIVs(true).build();

		 String encryptValue = new String(Base64.encode(encrypter.encrypt(plaintext.getBytes())));
		 return encryptValue;
	 }




}