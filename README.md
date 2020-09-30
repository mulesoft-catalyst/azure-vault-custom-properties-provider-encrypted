# Azure Vault Property Provider -- With Encrypted Client ID/Secret and Encoded EncryptKey


## Deploying to Exchange
Clone the project to your local, change the groupId to point your orgId. Issue `mvn deploy`.
Ensure that there is an entry in your settings.xml pertaining to Exchange2

## Local Install
For local install, give any groupId. Issue `mvn clean install -DskipTests`

## Using the module in a Mule4 Project
Add this dependency to your application pom.xml

```
<dependency>
	<groupId>${groupId}</groupId>
	<artifactId>azure-vault-custom-properties-provider-encrypted</artifactId>
	<version>${version}</version>
	<classifier>mule-plugin</classifier>
</dependency>
```

## Configuration


### Azure Application ClientId and Azure Application Secret Key
These should be provided as part of the wrapper.conf or during deployment.
Please note that these cannot be provided via Secure Properties Module as Azure Custom Properties module loads before Secure Properties module during Mule Startup.

Client ID, Secret from Azure need to be provided in an encrypted fashion (AES, CBC with Random IV) and the encryption should be done via Secure Properties tools jar (https://docs.mulesoft.com/mule-runtime/4.3/secure-configuration-properties#secure_props_tool)
*** If you need to use any other method for Encryption/decryption , you can update the decryption class "CustomCodeSecretProperties.java" 

### Additionally, you need to make the Secure Properties tools jar made available via Maven POM dependency. For Simplicity, I have placed the Jar as a custom asset within the Exchange and included it as dependency to perform the decryption. 


### Azure Vault Name
The vault name should be provided against which Mule will fetch keys from
Azure Vault Key Store.

### Encrypt Key 
should be base64 encoded key

### Example Config
A sample config.
```
		<azure-vault-property-provider:config name="Azure_Vault_Property_Provider_Config" doc:name="Azure Vault Property Provider Config" doc:id="3bb1983e-2453-4f0d-930a-5c234382fb68" >
		<azure-vault-property-provider:azure-vault applicationClientId="${example-azure-client-id}" applicationSecretKey="${example-azure-client-secret}" azureVaultName="${keyvaultmule}" encryptKey="${encryptKey}"/>
	</azure-vault-property-provider:config>
```

![alt text](encrypted_AzureVaultConnector.png)

## Usage

### Prefix
- For all purposes, this module relies on **azure-vault** as the prefix.
- Any property provided as **${azure-vault::}** will be referenced from Azure Vault Key Store.
You can change it at AzureVaultConfigurationPropertiesProvider

### Azure value can store 'n' number of  properties
- There can be `n` number of secrets in Azure Vault key store. The best way to access a particular key
`${azure-vault::key}`

### Example Usage

```
<set-variable
    value="${azure-vault::secretProperty1}"
    doc:name="Set Variable" doc:id="045a2583-411a-46fd-9352-60c64887970c"
    variableName="azureProperty1"/>

```

## Contributors
 Gaurav Talwadker / Original Azure Key Vault Properties Provider -- Srinivasan Raghunathan