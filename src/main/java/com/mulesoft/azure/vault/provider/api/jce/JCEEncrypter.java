package com.mulesoft.azure.vault.provider.api.jce;

import com.mulesoft.azure.vault.provider.api.exception.MuleEncryptionException;
import com.mulesoft.azure.vault.provider.api.exception.MuleInvalidAlgorithmConfigurationException;
import com.mulesoft.azure.vault.provider.api.exception.MuleInvalidKeyException;
import com.mulesoft.azure.vault.provider.api.keyfactories.EncryptionKeyFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class JCEEncrypter {
    private static final String INSTALL_JCE_MESSAGE = " You need to install the Java Cryptography Extension (JCE) " +
            "Unlimited Strength Jurisdiction Policy Files";

    private static final String ECB = "ECB";

    private final String provider;
    private final String transformation;
    private final EncryptionKeyFactory keyFactory;
    private final boolean useRandomIV;

    public JCEEncrypter(String transformation, EncryptionKeyFactory keyFactory) {
        this(transformation, null, keyFactory);
    }

    public JCEEncrypter(String transformation, String provider, EncryptionKeyFactory keyFactory) {
        this(transformation, provider, keyFactory, false);
    }

    public JCEEncrypter(String transformation, EncryptionKeyFactory keyFactory, boolean useRandomIV) {
        this(transformation, null, keyFactory, useRandomIV);
    }

    public JCEEncrypter(String transformation, String provider, EncryptionKeyFactory keyFactory, boolean useRandomIV) {
        this.transformation = transformation;
        this.provider = provider;
        this.keyFactory = keyFactory;
        this.useRandomIV = useRandomIV;
    }


    public byte[] decrypt(byte[] content) throws MuleEncryptionException {
        return runCipher(content, keyFactory.decryptionKey(), DECRYPT_MODE);
    }


    public byte[] encrypt(byte[] content) throws MuleEncryptionException {
        return runCipher(content, keyFactory.encryptionKey(), ENCRYPT_MODE);
    }

    protected AlgorithmParameterSpec getAlgorithmParameterSpec(IvParameterSpec ivParam) {
        return ivParam;
    }

    private byte[] runCipher(byte[] content, Key key, int mode) throws MuleEncryptionException {
        try {

            Cipher cipher = getCipher();
            String[] cipherParts = transformation.split("/");

            if (cipherParts.length >= 2 && ECB.equals(cipherParts[1])) {
                // ECB Encryption mode does not use IV
                cipher.init(mode, key);

                return cipher.doFinal(content);
            } else {
                SecureRandom secureRandom = new SecureRandom();

                // Create IV
                byte[] ivInByteArray = new byte[cipher.getBlockSize()];
                if (useRandomIV) {
                    if (mode == ENCRYPT_MODE) {
                        secureRandom.nextBytes(ivInByteArray);
                    } else {
                        ivInByteArray = Arrays.copyOfRange(content, 0, ivInByteArray.length);
                        content = Arrays.copyOfRange(content, ivInByteArray.length, content.length);
                    }
                } else {
                    ivInByteArray = Arrays.copyOfRange(key.getEncoded(), 0, ivInByteArray.length);
                }

                cipher.init(mode, key, getAlgorithmParameterSpec(new IvParameterSpec(ivInByteArray)), secureRandom);

                byte[] result = cipher.doFinal(content);

                if (mode == ENCRYPT_MODE && useRandomIV) {
                    byte[] byteArrayToConcatEncryptedDataAndIV = new byte[ivInByteArray.length + result.length];

                    arraycopy(ivInByteArray, 0, byteArrayToConcatEncryptedDataAndIV, 0, ivInByteArray.length);
                    arraycopy(result, 0, byteArrayToConcatEncryptedDataAndIV, ivInByteArray.length, result.length);

                    return byteArrayToConcatEncryptedDataAndIV;
                }

                return result;
            }

        } catch (InvalidAlgorithmParameterException e) {
            throw invalidAlgorithmConfigurationException(format("Wrong configuration for algorithm '%s'", transformation), e);
        } catch (NoSuchAlgorithmException e) {
            throw invalidAlgorithmConfigurationException(format("Cipher '%s' not found", transformation), e);
        } catch (NoSuchPaddingException e) {
            throw invalidAlgorithmConfigurationException(format("Invalid padding selected for cipher '%s'", transformation), e);
        } catch (NoSuchProviderException e) {
            throw invalidAlgorithmConfigurationException(format("Provider '%s' not found", provider), e);
        } catch (InvalidKeyException e) {
            throw handleInvalidKeyException(e, new String(key.getEncoded()));
        } catch (Exception e) {
            throw new MuleEncryptionException("Could not encrypt or decrypt the data.", e);
        }
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return provider == null ? Cipher.getInstance(transformation) : Cipher.getInstance(transformation, provider);
    }

    private MuleEncryptionException invalidAlgorithmConfigurationException(String message, Exception e) {
       boolean isJCEInstalled;
        try {
            int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
            isJCEInstalled =  maxKeyLen > 256;
        } catch (java.security.NoSuchAlgorithmException nsae) {
            isJCEInstalled = false;
        }
        if (isJCEInstalled) {
            message += INSTALL_JCE_MESSAGE;
        }

        return new MuleInvalidAlgorithmConfigurationException(message, e);
    }

    private MuleEncryptionException handleInvalidKeyException(InvalidKeyException e, String key) {
        String message = format("The key is invalid, please make sure it's of a supported size (actual is %s)", key.length());
        return new MuleInvalidKeyException(message, e);
    }
}
