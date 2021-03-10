package com.mulesoft.azure.vault.provider.api.keyfactories;

import java.security.Key;

public interface SymmetricKeyFactory extends EncryptionKeyFactory {
    @Override
    default Key decryptionKey() {
        return encryptionKey();
    }
}
