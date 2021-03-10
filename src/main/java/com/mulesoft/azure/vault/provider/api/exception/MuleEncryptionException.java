package com.mulesoft.azure.vault.provider.api.exception;

public class MuleEncryptionException extends Exception{
    public MuleEncryptionException(String message) {
        super(message);
    }

    public MuleEncryptionException(String message, Exception cause) {
        super(message, cause);
    }
}
