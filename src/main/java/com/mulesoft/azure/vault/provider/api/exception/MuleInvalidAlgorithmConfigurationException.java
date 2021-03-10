package com.mulesoft.azure.vault.provider.api.exception;

public class MuleInvalidAlgorithmConfigurationException extends MuleEncryptionException {

    public MuleInvalidAlgorithmConfigurationException(String message, Exception cause) {
        super(message, cause);
    }
}
