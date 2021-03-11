/*
 * (c) 2003-2018 MuleSoft, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSoft's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.azure.vault.provider.api;

import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.display.DisplayModel;
import org.mule.runtime.api.meta.model.display.PathModel;
import org.mule.runtime.extension.api.declaration.type.ExtensionsTypeLoaderFactory;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

/**
 * Declares extension for Secure Properties Configuration module
 *
 * @since 1.0
 */
public class AzureVaultConfigurationPropertiesExtensionLoadingDelegate implements ExtensionLoadingDelegate {

    // TODO replace with you extension name. This must be a meaningful name for this module.
    public static final String EXTENSION_NAME = "Azure Vault Property Provider";
    public static final String CONFIG_ELEMENT = "config";
    public static final String AZUREVALUT_CLIENT_PARAMETER_GROUP = "Azure Vault";

    @Override
    public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
        ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
                .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
                .withCategory(SELECT)
                .onVersion("1.0.0")
                .fromVendor("Microsoft Azure")
                // This defines a global element in the extension with name config
                .withConfig(CONFIG_ELEMENT);

        addAzureVaultParameters(configurationDeclarer);

    }

    /**
     * Add the Basic Connection parameters to the parameter list
     *
     * @param configurationDeclarer Extension {@link ConfigurationDeclarer}
     */
    private void addAzureVaultParameters(ConfigurationDeclarer configurationDeclarer) {

        ParameterGroupDeclarer addAzureVaultParametersGroup = configurationDeclarer
                .onParameterGroup(AZUREVALUT_CLIENT_PARAMETER_GROUP)
                .withDslInlineRepresentation(true);

        ClassTypeLoader typeLoader = ExtensionsTypeLoaderFactory.getDefault().createTypeLoader();


        addAzureVaultParametersGroup
                .withRequiredParameter("applicationClientId")
                .withDisplayModel(DisplayModel.builder().displayName("Encrypted Azure Application Client Id").build())
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(ExpressionSupport.SUPPORTED)
                .describedAs("Encrypted Azure Application Client Id");

        addAzureVaultParametersGroup
                .withRequiredParameter("applicationSecretKey")
                .withDisplayModel(DisplayModel.builder().displayName("Encrypted Azure Application Client Secret").build())
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(ExpressionSupport.SUPPORTED)
                .describedAs("Encrypted Azure Application Secret Key");
        addAzureVaultParametersGroup
                .withRequiredParameter("azureVaultName")
                .withDisplayModel(DisplayModel.builder().displayName("Azure Vault Name").build())
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(ExpressionSupport.SUPPORTED)
                .describedAs("Azure Vault Name");


        addAzureVaultParametersGroup
                .withRequiredParameter("encryptKey")
                .withDisplayModel(DisplayModel.builder().displayName("Encoded Encrypt Key").build())
                .ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(ExpressionSupport.SUPPORTED)
                .describedAs("Encoded Encrypt Key");

        addAzureVaultParametersGroup
                .withOptionalParameter("localfile").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(NOT_SUPPORTED)
                .withDisplayModel(DisplayModel.builder().path(new PathModel(PathModel.Type.FILE, false, PathModel.Location.EMBEDDED, new String[]{"yaml", "properties"}))
                        .build())
                .describedAs("Local property file which will be used for bypassing connection to Azure(Mandatory when ByPass flag is True)");


        addAzureVaultParametersGroup
                .withOptionalParameter("localPropertyProvider").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
                .withExpressionSupport(ExpressionSupport.SUPPORTED)
                .withDisplayModel(DisplayModel.builder().displayName("ByPass Azure Connection").build())
                .ofType(BaseTypeBuilder.create(JAVA).booleanType().build())
                .defaultingTo(Boolean.FALSE)
                .describedAs("Flag to bypass Azure connection");


    }
}


