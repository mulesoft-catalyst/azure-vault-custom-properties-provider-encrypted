package com.mulesoft.azure.vault.provider.api.keyfactories;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class SymmetricEncryptionKeyFactory implements SymmetricKeyFactory {
    private String algorithm;
    private byte[] key;

    public SymmetricEncryptionKeyFactory(String algorithm, String key) {
        validateKey(key);
        this.algorithm = algorithm;
        this.key = key.getBytes();
    }


    public Key encryptionKey() {
        return new SecretKeySpec(key, algorithm);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("If keystore is not defined then the key is considered to be " +
                    "an encryption key in Base64 encoding");
        }
    }
}
