 package com.mulesoft.azure.vault.provider.api;
import java.nio.charset.StandardCharsets;

import com.mulesoft.tools.SecurePropertiesTool;
public class CustomCodeSecretProperties{
	

	public static String decrypt(String encryptString, String decodedKey) {
		
		String decrryptedString=SecurePropertiesTool.applyOverString("decrypt", "AES", "CBC", decodedKey, true, encryptString);
		
		return decrryptedString;
	}

	
public static void main(String[] args) {
	
	
	String secretKey="asff/dUs=";
	String encodedSecret="sdfsdfs";
	
	// Decoding SecretKey from Base64
	byte[] secretkey_decodedBytes = java.util.Base64.getDecoder().decode(secretKey);
	secretKey = new String(secretkey_decodedBytes, StandardCharsets.UTF_8);
	
	System.out.println(secretKey);
	
	
	String decryptedValue=SecurePropertiesTool.applyOverString("decrypt", "AES", "CBC", secretKey, true, encodedSecret);
	System.out.println("Decrypted Value is "+decryptedValue);
	
 }
}