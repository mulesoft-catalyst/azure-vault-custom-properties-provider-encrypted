package com.mulesoft.azure.vault.provider.api.jce;

import com.mulesoft.azure.vault.provider.api.keyfactories.AsymmetricEncryptionKeyFactory;

import static com.mulesoft.azure.vault.provider.api.jce.EncryptionAlgorithm.RSA;
import static com.mulesoft.azure.vault.provider.api.jce.EncrypterBuilder.EncryptionMode.ECB;
import static com.mulesoft.azure.vault.provider.api.jce.EncryptionPadding.PKCS1PADDING;




public class AsymmetricEncrypterBuilder extends EncrypterBuilder{
    public JCEEncrypter build() {
        return new JCEEncrypter(RSA.name() + "/" + ECB.name() + "/" + PKCS1PADDING.name(), null,
                new AsymmetricEncryptionKeyFactory(RSA.name(), key), false);
    }
}
