# Azure Vault Property Provider -- With Encrypted Client ID/Secret and Encoded EncryptKey

## Deploying to Exchange
Clone the project to your local, change the groupId to point your orgId. Issue `mvn deploy`.
Ensure that there is an entry in your settings.xml pertaining to Exchange2

## Local Install
For local install, give any groupId. Issue `mvn clean install -DskipTests`

## Using the module in a Mule4 Project
Add this dependency to your application pom.xml
By default the group ID is given as com.mule.azurekv

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
Client ID, Secret from Azure need to be provided in an encrypted fashion (AES, CBC with Random IV with 256 bit encrypted key) and the encryption should be done via Secure Properties tools jar (https://docs.mulesoft.com/mule-runtime/4.3/secure-configuration-properties#secure_props_tool) *** If you need to use any other method for Encryption/decryption , you can update the decryption class "CustomCodeSecretProperties.java"


In this version we have removed the mule-secure-property-tool as dependency and decryption of Azure clientid and Azure client secret is support with in the AzureValutPropertyProvider



### Azure Vault Name
The vault name should be provided against which Mule will fetch keys from
Azure Vault Key Store.

### Encrypt Key
should be base64 encoded key
## localPropertyProvider
True or False to indicate if you want to byAzure Vault and load from the file with in the project. _localfile_ is Mandatory if the flag is encrypted_AzureVaultConnector
FYI -- Known Issue : From the UI screen in the configurations when selected as an Expression and reference is provided, it updates within the backend XML but doesnt show on the screen.
##localfile
Local File as property provider instead of Azure. Note that this Local file has to be provided as an Munit runtime argument within the Maven Command.

FYI -- Please note that Azure Key Vault doesnt allow special characters or dots within the name of the Keys. 

### Example Config
A sample config.
```
		<azure-vault-property-provider:config name="Azure_Vault_Property_Provider_Config" doc:name="Azure Vault Property Provider Config" doc:id="6e2bf9fb-25a4-4fca-8316-20656bc7a50d" >
    		<azure-vault-property-provider:azure-vault applicationClientId="${azure.clientId}" applicationSecretKey="${azure.secret}" azureVaultName="${azure.vaultName}" encryptKey="${azure.encryptionKey}" localPropertyProvider=${byPassAzureBooleanFlag} localfile=${localPropertyFile}/>
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

### TEST PROJECT

https://github.com/mulesoft-catalyst/azure-vault-encrypted-test-example

## Contributors
Srinivasan Raghunathan /Gaurav Talwadker
