package com.mulesoft.azure.vault.provider.api;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrDecrypt256BitTEST {
	
	
 
	private static final String initVector_encoded=System.getProperty("ivVectorKey");
	
	static byte[] iv_decodedBytes = java.util.Base64.getDecoder().decode(initVector_encoded);
	static String initVector  = new String(iv_decodedBytes);
	
    public static String encrypt(String strToEncrypt, String secretkey) 
    {
        try
        {
        	byte[] secretkey_decodedBytes = java.util.Base64.getDecoder().decode(secretkey);
        	secretkey  = new String(secretkey_decodedBytes);
        	
        	IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        	
        	SecretKeySpec skeySpec = new SecretKeySpec(secretkey.getBytes("UTF-8"), "AES");
        	
     
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
     
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
            return Base64.encodeBase64String(encrypted);
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secretkey) 
    {
        try
        {
        
        	// Decoding SecretKey from Base64
        	byte[] secretkey_decodedBytes = java.util.Base64.getDecoder().decode(secretkey);
        	secretkey  = new String(secretkey_decodedBytes);
        	
        	// Retrieving IV Vector
        	IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        	
        	
        	
            SecretKeySpec skeySpec = new SecretKeySpec(secretkey.getBytes("UTF-8"), "AES");
     
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(strToDecrypt));
     
            return new String(original);
        		
        } 
        catch (Exception e) 
        {
            System.out.println("Error while decrypting: " + e.getMessage());
        }
        return null;
    }
    
    
    public static void main(String[] args) 
    {
        final String secretKey = "JEImRSlIQE1iUWVUaFdtWnE0dDd3IXolQypGLUphTmQ=";
         
        //String originalString = "6a4854e2-2d49-44ec-8eb9-963b844e3033";
        
        //String encryptedString = AESEncrDecrypt256Bit.encrypt(originalString, secretKey) ;
        
        String encryptedString="BS24c+u6rfaj2yTHdkb6K0PgaB5BLFnVVOyhHkjVTeIm5ssasZGC7oDhl0AMGkuM";
        String decryptedString = AESEncrDecrypt256BitTEST.decrypt(encryptedString, secretKey) ;
         
        //System.out.println(originalString);
        System.out.println(encryptedString);
        System.out.println(decryptedString);
    }
}