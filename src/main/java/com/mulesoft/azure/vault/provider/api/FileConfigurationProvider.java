package com.mulesoft.azure.vault.provider.api;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.join;

public class FileConfigurationProvider {
    protected static final String PROPERTIES_EXTENSION = ".properties";
    protected static final String YAML_EXTENSION = ".yaml";
    private final static Logger LOGGER = LoggerFactory.getLogger(FileConfigurationProvider.class);

    private final static String AZUREVAULT_SECRET_PROPERTIES_PREFIX = "azure-vault::";



    protected final Map<String, Object> configurationAttributes = new LinkedHashMap<>();







    public Map<String, Object> yaml2map(String fileLocation) {
        initialise(fileLocation);
        return this.configurationAttributes != null ? this.configurationAttributes : null;
    }


    protected void initialise(String fileLocation) {
        if (!fileLocation.endsWith(PROPERTIES_EXTENSION) && !fileLocation.endsWith(YAML_EXTENSION)) {
            System.err.println(format("Configuration properties file %s must end with yaml or properties extension",
                    fileLocation));
        }
        InputStreamReader inStreamReader = null;
        try {
            inStreamReader = getResourceInputStreamReader(fileLocation);
            if (inStreamReader == null) {
                System.err.println(
                        format("Couldn't find configuration properties file %s neither on classpath or in file system",
                                fileLocation));
            }

            readAttributesFromFile(inStreamReader, fileLocation);
        } catch (Exception e) {

            System.err.println("Couldn't read from file " + fileLocation);
        } finally {
            IOUtils.closeQuietly(inStreamReader);
        }
    }

    protected InputStreamReader getResourceInputStreamReader(String fileLocation) throws IOException {
       return new InputStreamReader(loadFilesystemOrClasspathResource(fileLocation));
    }

    protected void readAttributesFromFile(InputStreamReader is, String fileLocation) throws IOException {
        if (fileLocation.endsWith(YAML_EXTENSION)) {
            Yaml yaml = new Yaml();
            Iterable<Object> yamlObjects = yaml.loadAll(is);
            try {
                yamlObjects.forEach(yamlObject -> {
                    createAttributesFromYamlObject(null, null, yamlObject);
                });
            } catch (ParserException e) {
                System.err.println(
                        "Error while parsing YAML configuration file. Check that all quotes are correctly closed.");
            }
        }else{
            Properties properties = new Properties();
            properties.load(is);
            configurationAttributes.putAll(properties.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> e.getKey().toString(),
                            e -> e.getValue().toString())));

        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void createAttributesFromYamlObject(String parentPath, Object parentYamlObject, Object yamlObject) {
        if (yamlObject instanceof List) {

            List list = (List) yamlObject;
            if (list.get(0) instanceof Map) {
                list.forEach(value -> createAttributesFromYamlObject(parentPath, yamlObject, value));
            } else {
                if (!(list.get(0) instanceof String)) {
                    System.err.println("List of complex objects are not supported as property values. Offending key is "
                            + parentPath);
                }
                String[] values = new String[list.size()];
                list.toArray(values);
                String value = join(",", list);
                configurationAttributes.put(parentPath, value);
            }
        } else if (yamlObject instanceof Map) {
            if (parentYamlObject instanceof List) {
                System.err.println(
                        "Configuration properties does not support type a list of complex types. Complex type keys are: "
                                + join(",", ((Map) yamlObject).keySet()));
            }
            Map<String, Object> map = (Map) yamlObject;
            map.entrySet().stream()
                    .forEach(entry -> createAttributesFromYamlObject(createKey(parentPath, entry.getKey()), yamlObject,
                            entry.getValue()));
        } else {
            if (!(yamlObject instanceof String)) {
                System.err.println(format(
                        "YAML configuration properties only supports string values, make sure to wrap the value with \" so you force the value to be an string. Offending property is %s with value %s",
                        parentPath, yamlObject));
            }
            if (parentPath == null) {
                if (((String) yamlObject).matches(".*:[^ ].*")) {
                    System.err.println(format(
                            "YAML configuration properties must have space after ':' character. Offending line is: %s",
                            yamlObject));
                } else {
                    System.err.println(format("YAML configuration property key must not be null. Offending line is %s",
                            yamlObject));
                }
            }
            String resultObject = createValue(parentPath, (String) yamlObject);
            configurationAttributes.put(parentPath, resultObject);
        }
    }

    protected String createKey(String parentKey, String key) {
        if (parentKey == null) {
            return key;
        }
        return parentKey + "." + key;
    }

    protected String createValue(String key, String value) {
        return value;
    }

    protected InputStream loadFilesystemOrClasspathResource(String resourceName) {

        try {

            File tryFile = new File(resourceName);

            if (tryFile.exists()) {
                LOGGER.debug("Found resource {} in filesystem, loading it...", resourceName);
                return new FileInputStream(tryFile);
            }

        } catch (IOException ex) {
            LOGGER.error("Error while trying to load resource from filesystem or classpath...", ex);
            throw new RuntimeException(ex);
        }

        return loadClasspathResource(resourceName);
    }

    protected InputStream loadClasspathResource(String resourceName) {

        InputStream is = null;

        if (Thread.currentThread().getContextClassLoader() != null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
            LOGGER.warn("Could not find resource: {} either on classpath or filesystem...", resourceName);
        }

        return is;

    }


}
