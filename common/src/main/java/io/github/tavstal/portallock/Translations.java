package io.github.tavstal.portallock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tavstal.portallock.utils.ConfigUtils;
import io.github.tavstal.portallock.utils.ModUtils;
import net.minecraft.network.chat.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * The {@code Translations} class provides a mechanism to manage and retrieve translations from a configuration source.
 * It loads translations as needed, caches them, and supports formatted messages with optional arguments.
 */
public class Translations {
    private static final String minecraftRootPath = System.getProperty("user.dir");
    private static final Path translationsDirPath =
            Paths.get(minecraftRootPath, CommonClass.IsPlugin() ? "plugins" : "config", CommonClass.MOD_NAME, "translations");

    /** A map that stores translation key-value pairs, initialized on first access. */
    private static JsonObject _translations = null;

    /**
     * Initializes necessary resources, settings, or configurations for the application.
     * This method should be called at the start of the application lifecycle to
     * ensure that all required components are set up.
     */
    public static void init() {
        InputStream inputStream = null;
        String locale =  CommonClass.CONFIG().Language;

        if (!Files.exists(translationsDirPath))
            try
            {
                Files.createDirectory(translationsDirPath);
            }
            catch (IOException ex)
            {
                CommonClass.LOG.warn("Failed to create lang directory.");
                CommonClass.LOG.error(ex.getMessage());
                return;
            }

        Path filePath = Paths.get(translationsDirPath.toString(), String.format("%s.json", locale));
        if (!Files.exists(filePath))
        {
            try {
                // Get the resource URL
                String fileResource = MessageFormat.format("/lang/{0}.json", locale);
                inputStream = CommonClass.class.getResourceAsStream(fileResource);
                if (inputStream == null) {
                    throw new IOException("Resource file not found: " + fileResource);
                }
                // Copy the file
                Files.copy(inputStream, filePath);

                // Close the input stream
                inputStream.close();
            } catch (IOException e) {
                CommonClass.LOG.error(e.getMessage());
                filePath = Paths.get(translationsDirPath.toString(), "en.json");
            }
        }

        try
        {
            inputStream = new FileInputStream(filePath.toFile());
        }
        catch (FileNotFoundException ex)
        {
            CommonClass.LOG.error(String.format("Failed to get localization file. Path: %s", filePath));
            return;
        }
        catch (Exception ex)
        {
            CommonClass.LOG.warn("Unknown error happened while reading locale file.");
            CommonClass.LOG.error(ex.getMessage());
            return;
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            _translations = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception ex) {
            CommonClass.LOG.warn("Unknown error happened while reading json locale.");
            CommonClass.LOG.error(ex.getMessage());
        }
    }
    
    /**
     * Retrieves the translation for a specified key.
     *
     * @param key the key for the translation to retrieve
     * @return the translation string associated with the key, or {@code null} if the key does not exist
     */
    public static String GetLocale(String key) {
        try {
            var result = ConfigUtils.getJsonValue(_translations, key);
            if (result == null)
                return null;
            return result.toString();
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GGetLocale:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Retrieves the translation for a specified key and formats it using the provided arguments.
     *
     * @param key the key for the translation to retrieve
     * @param args the arguments to format the translation string with
     * @return the formatted translation string, or {@code null} if the key does not exist or formatting fails
     */
    public static String GetLocale(String key, Object... args) {
        try {
            var result = ConfigUtils.getJsonValue(_translations, key);
            if (result == null)
                return null;
            return MessageFormat.format(result.toString(), args);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetLocale (args):");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Retrieves the translation for a specified key with a prefix appended to it.
     *
     * @param key the key for the translation to retrieve
     * @return the prefixed translation string, or {@code null} if the key or prefix does not exist
     */
    public static String GetLocalePrefix(String key) {
        try {
            var result = ConfigUtils.getJsonValue(_translations, key);
            if (result == null)
                return null;
            return GetLocale("general.prefix") + result.toString();
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetLocalePrefix:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Retrieves the translation for a specified key with a prefix and formats it using the provided arguments.
     *
     * @param key the key for the translation to retrieve
     * @param args the arguments to format the translation string with
     * @return the prefixed and formatted translation string, or {@code null} if the key or prefix does not exist or formatting fails
     */
    public static String GetLocalePrefix(String key, Object... args) {
        try {
            var result = ConfigUtils.getJsonValue(_translations, key);
            if (result == null)
                return null;
            return GetLocale("general.prefix") + MessageFormat.format(result.toString(), args);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetLocalePrefix (args):");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Retrieves a translation as a Component for the given key.
     *
     * @param key The translation key.
     * @return A Component representing the translation, or null if an error occurs.
     */
    public static Component GetLocaleComp(String key) {
        try {
            return ModUtils.Literal(GetLocale(key));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetLocaleComp:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return ModUtils.Literal("§cFailed to get the key, please report it to an admin.");
        }
    }

    /**
     * Retrieves a formatted translation as a Component for the given key with arguments.
     *
     * @param key  The translation key.
     * @param args The arguments to format the translation.
     * @return A Component representing the formatted translation, or null if an error occurs.
     */
    public static Component GetLocaleComp(String key, Object... args) {
        try {
            return ModUtils.Literal(GetLocale(key, args));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetLocaleComp (args):");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return ModUtils.Literal("§cFailed to get the key, please report it to an admin.");
        }
    }

    /**
     * Retrieves a translation as a Component with a prefix for the given key.
     *
     * @param key The translation key.
     * @return A Component representing the translation with a prefix, or null if an error occurs.
     */
    public static Component GetLocaleCompPrefix(String key) {
        try {
            return ModUtils.Literal(GetLocalePrefix(key));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetLocaleCompPrefix:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return ModUtils.Literal("§cFailed to get the key, please report it to an admin.");
        }
    }

    /**
     * Retrieves a formatted translation as a Component with a prefix for the given key and arguments.
     *
     * @param key  The translation key.
     * @param args The arguments to format the translation.
     * @return A Component representing the formatted translation with a prefix, or null if an error occurs.
     */
    public static Component GetLocaleCompPrefix(String key, Object... args) {
        try {
            return ModUtils.Literal(GetLocalePrefix(key, args));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetLocaleCompPrefix (args):");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return ModUtils.Literal("§cFailed to get the key, please report it to an admin.");
        }
    }
}

