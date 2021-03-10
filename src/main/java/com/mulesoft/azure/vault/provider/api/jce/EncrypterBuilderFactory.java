package com.mulesoft.azure.vault.provider.api.jce;

public interface EncrypterBuilderFactory {
    EncrypterBuilder createFor(EncryptionAlgorithm algorithm);
}
