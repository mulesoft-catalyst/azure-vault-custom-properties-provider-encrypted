package com.mulesoft.azure.vault.provider.api.jce;

import com.mulesoft.azure.vault.provider.api.keyfactories.SymmetricEncryptionKeyFactory;
import static com.mulesoft.azure.vault.provider.api.jce.EncryptionPadding.PKCS5Padding;


public class SymmetricEncrypterBuilder extends EncrypterBuilder{
    private EncryptionAlgorithm encryptionAlgorithm;

    public SymmetricEncrypterBuilder(EncryptionAlgorithm encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    @Override
    public JCEEncrypter build() {
        return new JCEEncrypter(encryptionAlgorithm.name() + "/" + mode.name() + "/" + PKCS5Padding.name(), null,
                new SymmetricEncryptionKeyFactory(encryptionAlgorithm.name(), key), useRandomIVs);
    }


}
