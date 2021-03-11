package com.mulesoft.azure.vault.provider.api.jce;

public abstract class EncrypterBuilder {

    public enum EncryptionMode {
        CBC, CFB, ECB, OFB;
    }

    protected EncryptionMode mode;
    protected String key;
    protected boolean useRandomIVs;

    public EncrypterBuilder using(EncryptionMode mode) {
        this.mode = mode;
        return this;
    }

    public abstract JCEEncrypter build();

    public EncrypterBuilder forKey(String key) {
        this.key = key;
        return this;
    }

    public EncrypterBuilder useRandomIVs(boolean useRandomIVs) {
        this.useRandomIVs = useRandomIVs;
        return this;
    }


}
