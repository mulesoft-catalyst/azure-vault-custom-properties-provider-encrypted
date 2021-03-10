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



/**
public static void main(String[] args) {
	
	try {
		String secretKey = "S01sOTFhSnRLVzRVenpmdTRCZFRZemlEVkZMS0gzRmk=";
		String encodedSecret = "+WIMWuIrD9cZYwBryTOuZbJI1zV4/93cLDilXLe/bnyHHzzLYxIuXy/By47U7B0pUgfoaVADq2vIcQqyod8KHQ==";
		// Decoding SecretKey from Base64
		byte[] secretkey_decodedBytes = Base64.decode(secretKey);
		secretKey = new String(secretkey_decodedBytes, StandardCharsets.UTF_8);
		System.out.println(secretKey);

	//	String encryptedValue=CustomCodeSecretProperties.encrypt("27989859-b52f-4069-92af-51b51f5e830b", secretKey);
	//	System.out.println(encryptedValue);
		String decryptedValue = CustomCodeSecretProperties.decrypt(encodedSecret, secretKey);

		System.out.println("Decrypted Value is " + decryptedValue);
	}catch (Exception e){
		e.printStackTrace();
	}
	
 }
 **/
}