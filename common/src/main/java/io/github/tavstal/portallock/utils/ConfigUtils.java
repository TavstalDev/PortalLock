package io.github.tavstal.portallock.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.tavstal.portallock.CommonClass;
import io.github.tavstal.portallock.CommonConfig;
import io.github.tavstal.portallock.Translations;
import io.github.tavstal.portallock.models.ConfigField;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

/**
 * Utility class for handling configuration-related operations.
 *
 * <p>
 * This class provides methods to load, save, and manipulate configuration
 * settings in a structured way. It may also include methods for
 * validating configuration values and managing default settings.
 * </p>
 *
 * @since 1.0
 */
public class ConfigUtils {
    private static final String minecraftRootPath = System.getProperty("user.dir");
    private static final Path configFilePath = CommonClass.IsPlugin() ?
            Paths.get(minecraftRootPath, "plugins", CommonClass.MOD_NAME, CommonClass.MOD_ID + "-config.jsonc")
            :
            Paths.get(minecraftRootPath, "config", CommonClass.MOD_NAME, CommonClass.MOD_ID + "-config.jsonc");
    private static final Path translationsFilePath = CommonClass.IsPlugin() ?
            Paths.get(minecraftRootPath, "plugins", CommonClass.MOD_NAME, "translations", getLangKey() + ".json")
            :
            Paths.get(minecraftRootPath, "config", CommonClass.MOD_NAME, "translations", getLangKey() + ".json");

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting() // Enables pretty printing
            .create();

    private static String getLangKey() {
        String lang = "en";
        if (!CommonClass.CONFIG().Language.isEmpty()) {
            lang = CommonClass.CONFIG().Language;
        }
        return  lang;
    }

    /**
     * Loads the configuration settings from a predefined configuration file.
     *
     * <p>This method reads the configuration data, typically stored in JSON
     * or YAML format, and deserializes it into a {@link CommonConfig} object,
     * which contains the application's configurable settings.</p>
     *
     * <p>If the configuration file does not exist or cannot be read, this
     * method may return a default instance of {@link CommonConfig} with default
     * values, or it may throw an exception based on the implementation.</p>
     *
     * @return An instance of {@link CommonConfig} containing the loaded configuration
     * settings.
     */
    public static CommonConfig loadConfig() {
        CommonConfig result = new CommonConfig();

        try {
            // Load Defaults
            var configFile = configFilePath.toFile();
            if (!configFile.exists()) {
                try {
                    File parentDir = configFile.getParentFile();
                    if (!parentDir.exists()) {
                        boolean mkdirs = parentDir.mkdirs();
                    }

                    saveConfig(result);  // Save the new config file with default values
                    return result;
                } catch (Exception e) {
                    CommonClass.LOG.error("Failed to load default configs.");
                    CommonClass.LOG.error(e.getLocalizedMessage());
                    return result;
                }
            }
            
            result = deserializeJSONC(Files.readAllLines(configFilePath), CommonConfig.class);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in loadConfig()");
            CommonClass.LOG.error(ex.getLocalizedMessage());
        }

        return result;
    }

    /**
     * Loads translations from a JSON file into a map.
     *
     * @return a map containing translations, with each key-value pair from the file
     */
    public static Map<String, String> loadTranslations() {
        Map<String, String> result = Translations.GetDefaultTranslations();

        try {
            // Load Defaults
            var translationsFile = translationsFilePath.toFile();
            if (!translationsFile.exists()) {
                try {
                    File parentDir = translationsFile.getParentFile();
                    if (!parentDir.exists()) {
                        boolean mkdirs = parentDir.mkdirs();
                    }

                    saveTranslations(result);  // Save the new config file with default values
                    return result;
                } catch (Exception e) {
                    CommonClass.LOG.error("Failed to load default translations.");
                    CommonClass.LOG.error(e.getLocalizedMessage());
                    return result;
                }
            }

            result = gson.fromJson(Files.readString(configFilePath), new TypeToken<Map<String, String>>(){}.getType());
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in loadTranslations()");
            CommonClass.LOG.error(ex.getLocalizedMessage());
        }

        return result;
    }

    /**
     * Saves the given configuration settings to a configuration file.
     *
     * <p>This method serializes the provided {@link CommonConfig} object
     * and writes it to the application's configuration file in JSON or YAML format,
     * preserving the current configuration state.</p>
     *
     * <p>If the file does not exist, this method may create it. If the file
     * already exists, it may overwrite it with the updated configuration values.</p>
     *
     * @param config The {@link CommonConfig} instance containing the settings
     *               to be saved.
     */
    public static void saveConfig(CommonConfig config) {
        try (FileWriter writer = new FileWriter(configFilePath.toFile())) {
            writer.write(serializeJSONC(config, 1, true));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in saveConfig()");
            CommonClass.LOG.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Saves the given translations map to a JSON file.
     *
     * @param translations the map of translations to save
     */
    public static void saveTranslations(Map<String, String> translations) {
        try (FileWriter writer = new FileWriter(translationsFilePath.toFile())) {
            writer.write(serialize(translations));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in saveTranslations()");
            CommonClass.LOG.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Serializes an object to a JSONC (JSON with comments) format.
     * <p>
     * This method converts the specified object into a JSONC string representation.
     * JSONC allows for comments within the JSON structure, making it easier to
     * annotate the data. The method can control the indentation level for better
     * readability and can also add start and end brackets depending on the
     * specified parameter.
     * </p>
     * @param obj          The object to serialize to JSONC. This can be any
     *                     object that can be converted to JSON, such as
     *                     primitives, arrays, or collections.
     * @param indentLevel  The level of indentation to use for formatting the
     *                     output. A value of 0 indicates no indentation, while
     *                     higher values increase the indentation level for
     *                     nested structures.
     * @param addStartBrackets  A boolean flag indicating whether to include
     *                          starting and ending brackets in the output.
     *                          If true, the serialized output will be enclosed
     *                          in curly braces (for objects).
     *                          If false, no brackets will be added.
     *
     * @return A string representation of the object in JSONC format.
     *         The string will include comments, proper indentation,
     *         and, if specified, starting and ending brackets.
     *
     * @throws IllegalArgumentException if the input object cannot be
     *         serialized to JSONC.
     */
    private static String serializeJSONC(Object obj, int indentLevel, boolean addStartBrackets) {
        StringBuilder result = new StringBuilder();
        String indent = "  ".repeat(indentLevel); // Create indentation string
        try {
            Class<?> objClass = obj.getClass();
            if (addStartBrackets)
                result.append("{\n");
            int fieldIndex = 0;
            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);  // Allow access to private fields
                String endCharacter = "";
                if (fieldIndex + 1 != objClass.getDeclaredFields().length)
                    endCharacter = ",";

                try {
                    Object value = field.get(obj);
                    String valueSerialized = serialize(value);
                    String fieldName = field.getName();

                    // Add comments
                    if (field.isAnnotationPresent(ConfigField.class)) {
                        var configField = field.getAnnotation(ConfigField.class);
                        String comment = configField.comment();
                        if (!comment.isBlank()) {
                            if (comment.contains("\n")) {
                                comment = comment.replaceAll("\n", "\n" + indent + "// ");
                            }
                            result.append(MessageFormat.format("{0}// {1}\n", indent, comment));
                        }
                    }

                    if (value instanceof Collection<?> collection) {
                        // Handle collections
                        result.append(MessageFormat.format("{0}\"{1}\":\n", indent, fieldName));
                        int collectionIndex = 0;
                        result.append(MessageFormat.format("{0}[\n", indent));
                        for (Object item : collection) {
                            result.append(MessageFormat.format("{0}  '{'\n", indent)); // Indent for items
                            result.append(serializeJSONC(item, indentLevel + 2, false));
                            if (collection.size() != collectionIndex + 1)
                                result.append(MessageFormat.format("{0}  '}',\n", indent));
                            else
                                result.append(MessageFormat.format("{0}  '}'\n", indent));
                            collectionIndex++;
                        }
                        result.append(MessageFormat.format("{0}]{1}\n", indent, endCharacter));
                    } else if (!field.getType().isPrimitive() && !field.getType().equals(String.class)) {
                        // Handle nested objects
                        result.append(MessageFormat.format("{0}\"{1}\":\n", indent, fieldName));
                        result.append(MessageFormat.format("{0}'{'\n", indent));
                        result.append(serializeJSONC(value, indentLevel + 1, false));
                        result.append(MessageFormat.format("{0}'}'{1}\n", indent, endCharacter));
                    } else {
                        result.append(MessageFormat.format("{0}\"{1}\": {2}{3}\n", indent, fieldName, valueSerialized, endCharacter));
                    }


                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error accessing field: " + field.getName(), e);
                }
                fieldIndex++;
            }
            if (addStartBrackets)
                result.append("}");
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in serializeJSONC()");
            CommonClass.LOG.error(ex.getLocalizedMessage());
        }
        return result.toString();
    }

    /**
     * Deserializes a list of JSONC lines into an object of the specified type.
     * <p>
     * This method removes comments from the provided JSONC lines and then converts the
     * resulting JSON string into an object of type {@code T}. The comments can be
     * single-line (//) or multi-line (/* ... /*).
     * </p>
     * @param jsoncLines the list of strings representing JSONC lines, which may include comments
     * @param clazz the class of type {@code T} to which the JSON should be deserialized
     * @param <T> the type of the object to be deserialized
     * @return an instance of {@code T} populated with data from the JSON, or {@code null}
     *         if deserialization fails due to malformed JSON or other issues
     */
    private static <T> T deserializeJSONC(List<String> jsoncLines, Class<T> clazz) {
        StringBuilder jsonBuilder = new StringBuilder();
        boolean foundMultilineComment = false;
        for (String line : jsoncLines) {
            // Check for multiline comment start
            if (line.contains("/*")) {
                foundMultilineComment = true;
                // Keep the part of the line before the multiline comment
                jsonBuilder.append(line, 0, line.indexOf("/*"));
            }

            // Check for single-line comments and ignore them
            if (line.contains("//") && !foundMultilineComment) {
                jsonBuilder.append(line, 0, line.indexOf("//"));
                continue;
            }

            // If we are in a multiline comment, check for its end
            if (foundMultilineComment) {
                foundMultilineComment = !line.contains("*/");
                // If the line contains the end of the comment, append the part after it
                if (!foundMultilineComment) {
                    int endCommentIndex = line.indexOf("*/") + 2;
                    if (endCommentIndex < line.length()) {
                        jsonBuilder.append(line.substring(endCommentIndex));
                    }
                }
                continue;
            }

            // If there are no comments, append the line as is
            jsonBuilder.append(line);
        }

        return gson.fromJson(jsonBuilder.toString(), clazz);
    }

    /**
     * Serializes an object to a JSON string.
     *
     * @param obj The object to be serialized.
     * @return The JSON representation of the object as a string.
     */
    private static String serialize(Object obj) {
        return gson.toJson(obj);
    }
}