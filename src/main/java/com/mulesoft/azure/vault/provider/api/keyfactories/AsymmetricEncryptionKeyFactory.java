package com.mulesoft.azure.vault.provider.api.keyfactories;

import org.bouncycastle.util.encoders.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AsymmetricEncryptionKeyFactory implements EncryptionKeyFactory {

    private String algorithm;
    private byte[] key;

    public AsymmetricEncryptionKeyFactory(String algorithm, String key) {
        validateKey(key);
        this.algorithm = algorithm;
        this.key = Base64.decode(key);
    }


    public Key encryptionKey() {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Could not build the Encryption key", e);
        }
    }


    public Key decryptionKey() {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Could not build the decryption key", e);
        }
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("If keystore is not defined then the key is considered to be " +
                    "an encryption key in Base64 encoding");
        }
    }
}

