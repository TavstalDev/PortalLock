package io.github.tavstal.portallock.utils;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.github.tavstal.portallock.CommonClass;
import io.github.tavstal.portallock.CommonConfig;
import io.github.tavstal.portallock.models.ConfigField;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
    private  static final String minecraftRootPath = System.getProperty("user.dir");
    private static final Path configFilePath = CommonClass.IsPlugin() ? Paths.get(minecraftRootPath, "plugins", CommonClass.MOD_NAME, CommonClass.MOD_ID + ".toml") : Paths.get(minecraftRootPath, "config", CommonClass.MOD_ID + ".toml");

    /**
     * Loads the configuration settings from the configuration file.
     *
     * <p>
     * This method reads the configuration file and populates a
     * {@link CommonConfig} object with the values specified in the file.
     * If the configuration file does not exist or is invalid,
     * default values may be applied.
     * </p>
     *
     * @return a {@link CommonConfig} object containing the loaded configuration settings.
     */
    public static CommonConfig LoadConfig() {
        try {
            File configFile = configFilePath.toFile();
            CommonConfig config = null;

            if (!configFile.exists()) {
                try {
                    File parentDir = configFile.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    // Create a new instance of the config class with default values
                    config = CommonConfig.class.getDeclaredConstructor().newInstance();
                    SaveConfig(config);  // Save the new config file with default values
                    return config;
                } catch (Exception e) {
                    CommonClass.LOG.error("Failed to load default configs.");
                    CommonClass.LOG.error(e.getLocalizedMessage());
                    return null;
                }
            }

            CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile).sync().build();
            fileConfig.load();

            try {
                config = CommonConfig.class.getDeclaredConstructor().newInstance();  // Create an instance of the config class
                // Use reflection to load all fields automatically
                for (Field field : CommonConfig.class.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = fileConfig.getOrElse(field.getName(), field.get(config));
                    field.set(config, value);  // Assign the value from the file to the field
                }
            } catch (Exception e) {
                CommonClass.LOG.error("Failed to load configs.");
                CommonClass.LOG.error(e.getLocalizedMessage());
            }

            return config;
        }
        catch (Exception ex)
        {
            CommonClass.LOG.error("Error during executing method 'LoadConfig':");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Saves the specified configuration settings to the configuration file.
     *
     * <p>
     * This method serializes the provided {@link CommonConfig} object
     * and writes its values to the configuration file. If the file already
     * exists, it will be overwritten. Comments will be added based on the
     * {@link ConfigField} annotations in the config fields.
     * </p>
     *
     * @param config the {@link CommonConfig} object containing the configuration settings
     *               to be saved. Must not be {@code null}.
     * @throws IllegalArgumentException if the provided config object is {@code null}.
     */
    public static void SaveConfig(CommonConfig config)
    {
        File configFile = configFilePath.toFile();
        StringBuilder tomlContent = new StringBuilder();
        try (FileWriter fileWriter = new FileWriter(configFile))
        {
            CommentedFileConfig commentedFileConfig = CommentedFileConfig.builder(configFile)
                    .sync()
                    .build();

            // Get fields and sort them by the ConfigField annotation
            List<Field> sortedFields = Arrays.stream(config.getClass().getDeclaredFields())
                    .peek(field -> field.setAccessible(true)) // Make private fields accessible
                    .sorted(Comparator.comparingInt(field -> {
                        ConfigField annotation = field.getAnnotation(ConfigField.class);
                        return annotation != null ? annotation.order() : Integer.MAX_VALUE;
                    }))
                    .toList();

            // Use reflection to save all fields automatically
            for (Field field : sortedFields) {
                try {
                    ConfigField annotation = field.getAnnotation(ConfigField.class);
                    if (annotation != null) {
                        String comment = annotation.comment();

                        // Add comment to the TOML content
                        if (!comment.isBlank())
                            tomlContent.append("# ").append(comment).append("\n");
                    }
                    tomlContent.append(field.getName()).append(" = ");
                    Object value = field.get(config);
                    tomlContent.append(value instanceof String ? "\"" + value + "\"" : value);
                    tomlContent.append("\n\n");
                } catch (IllegalAccessException e) {
                    CommonClass.LOG.error("Failed to load config variable values while saving.");
                    CommonClass.LOG.error(e.getLocalizedMessage());
                }
            }

            // Write the content to the file
            fileWriter.write(tomlContent.toString());
        }
        catch (Exception e) {
            CommonClass.LOG.error("Failed to save configs.");
            CommonClass.LOG.error(e.getLocalizedMessage());
        }
    }
}