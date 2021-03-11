package com.mulesoft.azure.vault.provider.api.keyfactories;

import java.security.Key;

public interface EncryptionKeyFactory {
    Key encryptionKey();
    Key decryptionKey();

}
