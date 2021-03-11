package com.mulesoft.azure.vault.provider.api.exception;

public class MuleInvalidKeyException extends MuleEncryptionException {

    public MuleInvalidKeyException(String message, Exception cause) {
        super(message, cause);
    }
}