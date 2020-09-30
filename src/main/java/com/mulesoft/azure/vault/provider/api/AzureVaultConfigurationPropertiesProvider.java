package com.mulesoft.azure.vault.provider.api;

import com.microsoft.aad.adal4j.AuthenticationException;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

public class AzureVaultConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(AzureVaultConfigurationPropertiesProvider.class);

    private final static String AZUREVAULT_DNS_PREFIX = ".vault.azure.net";

    private final static String HTTPS_PROTOCOL = "https://";

    private final static String AZUREVAULT_SECRET_PROPERTIES_PREFIX = "azure-vault::";

    private final static Pattern AZURE_VAULT_SECRETS_PATTERN = Pattern.compile("\\$\\{" + AZUREVAULT_SECRET_PROPERTIES_PREFIX + "[^}]*}");

    private final KeyVaultClient azureKeyVaultClient;

    private final String vaultName;

    public AzureVaultConfigurationPropertiesProvider(KeyVaultClient azureKeyVaultClient, String vaultName) {
        this.azureKeyVaultClient = azureKeyVaultClient;
        this.vaultName = vaultName;
    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        LOGGER.info("==============================" + configurationAttributeKey);
        String propertyKey = configurationAttributeKey;
        if (propertyKey.startsWith(AZUREVAULT_SECRET_PROPERTIES_PREFIX)) {
            final String propertyActualKey = propertyKey.substring(AZUREVAULT_SECRET_PROPERTIES_PREFIX.length());
            LOGGER.info("==============================" + propertyActualKey);
            try {
                final String value = getSecretFromVault(propertyActualKey);
                if (value != null) {
                    return Optional.of(new ConfigurationProperty() {

                        @Override
                        public Object getSource() {
                            return "Azure Key Vault";
                        }

                        @Override
                        public Object getRawValue() {
                            return value;
                        }

                        @Override
                        public String getKey() {
                            return propertyActualKey;
                        }
                    });
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }


    @Override
    public String getDescription() {
        return "Azure Vault Property Provider";
    }

    private String getSecretFromVault(String key) {

        String secretValue = null;
        try {
            SecretBundle secret = azureKeyVaultClient.getSecret(HTTPS_PROTOCOL + vaultName + AZUREVAULT_DNS_PREFIX, key);
            

            if (secret != null) {
                secretValue = secret.value();
            } else {
                LOGGER.error("secret key not found : " + key);
                throw new IllegalArgumentException("secret value not found for the key");

            }

        }  catch (Exception e) {

            System.out.println("Error Occured " + e.getMessage());

        }
        return secretValue;

    }
}
