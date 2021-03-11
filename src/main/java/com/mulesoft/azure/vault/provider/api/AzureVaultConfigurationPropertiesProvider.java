package com.mulesoft.azure.vault.provider.api;

import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.models.SecretBundle;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class AzureVaultConfigurationPropertiesProvider implements ConfigurationPropertiesProvider {

    private final static Logger LOGGER = LoggerFactory.getLogger(AzureVaultConfigurationPropertiesProvider.class);

    private final static String AZUREVAULT_DNS_PREFIX = ".vault.azure.net";

    private final static String HTTPS_PROTOCOL = "https://";

    private final static String AZUREVAULT_SECRET_PROPERTIES_PREFIX = "azure-vault::";

    private final static Pattern AZURE_VAULT_SECRETS_PATTERN = Pattern.compile("\\$\\{" + AZUREVAULT_SECRET_PROPERTIES_PREFIX + "[^}]*}");

    private  KeyVaultClient azureKeyVaultClient;

    private  String vaultName;
    private  Map<String, Object> fileLocalPropertyMap;
    private  boolean byPassAzure;


    public AzureVaultConfigurationPropertiesProvider(KeyVaultClient azureKeyVaultClient, String vaultName,boolean byPassAzure) {
        this.azureKeyVaultClient = azureKeyVaultClient;
        this.vaultName = vaultName;
        this.byPassAzure=byPassAzure;
    }

    public AzureVaultConfigurationPropertiesProvider(Map<String, Object> fileLocalPropertyMap,boolean byPassAzure) {
        this.fileLocalPropertyMap = fileLocalPropertyMap;
        this.byPassAzure=byPassAzure;

    }

    @Override
    public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
        LOGGER.debug("==============================" + configurationAttributeKey);
        String propertyKey = configurationAttributeKey;
        if (propertyKey.startsWith(AZUREVAULT_SECRET_PROPERTIES_PREFIX)) {
            final String propertyActualKey = propertyKey.substring(AZUREVAULT_SECRET_PROPERTIES_PREFIX.length());
            LOGGER.info("==============================" + propertyActualKey);

            try {
                final String value;
                if(!byPassAzure) {
                     value = getSecretFromVault(propertyActualKey);
                }else{
                     value = getPropertyValueFromMap(propertyActualKey);
                }
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

            LOGGER.error("Error Occured " + e.getMessage());

        }
        return secretValue;

    }

    private String getPropertyValueFromMap(String key){
        String propValue=null;
        if(fileLocalPropertyMap != null) {
            propValue = (String) fileLocalPropertyMap.get(key).toString();
        } else{
            LOGGER.error("secret key not found : " + key);

        }
        return propValue;
    }
}
