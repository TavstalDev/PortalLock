package io.github.tavstal.portallock;

import io.github.tavstal.portallock.utils.ConfigUtils;
import io.github.tavstal.portallock.utils.ModUtils;
import net.minecraft.network.chat.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Translations} class provides a mechanism to manage and retrieve translations from a configuration source.
 * It loads translations as needed, caches them, and supports formatted messages with optional arguments.
 */
public class Translations {
    // TODO: Auto download translations

    /**
     * Holds default translations with predefined key-value pairs.
     */
    private static final Map<String, String> _defaultTranslations = new HashMap<>() {{
        put("time_days", "§e{0} §aday(s)");
        put("time_hours", "§e{0} §ahour(s)");
        put("time_minutes", "§e{0} §aminute(s)");
        put("time_seconds", "§e{0} §asecond(s)");
        put("command_syntax", "§cWrong syntax! §aUsage: /{0} {1}");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("error_dimension_not_found", "§cFailed to get the §e{0}§c dimension.");
        put("error_dimension_already_exist", "§cThe §e{0} §cdimension already exists.");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
        put("", "");
    }};

    /**
     * Retrieves the map containing default translation entries.
     *
     * @return the default translations map, containing predefined key-value pairs
     */
    public static Map<String, String> GetDefaultTranslations() {
        return  _defaultTranslations;
    }

    /** A map that stores translation key-value pairs, initialized on first access. */
    private static Map<String, String> _translations = null;

    /**
     * Retrieves the translations map, loading it from the configuration if not already loaded.
     *
     * @return a map containing translation key-value pairs
     */
    public static Map<String, String> GetTranslations() {
        if (_translations == null) {
            _translations = ConfigUtils.loadTranslations();
        }
        return _translations;
    }

    /**
     * Initializes necessary resources, settings, or configurations for the application.
     * This method should be called at the start of the application lifecycle to
     * ensure that all required components are set up.
     */
    public static void init() {
        if (_translations == null) {
            _translations = ConfigUtils.loadTranslations();
        }
    }

    /**
     * Retrieves the translation for a specified key.
     *
     * @param key the key for the translation to retrieve
     * @return the translation string associated with the key, or {@code null} if the key does not exist
     */
    public static String GetTranslation(String key) {
        try {
            return GetTranslations().get(key);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetTranslation:");
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
    public static String GetTranslation(String key, Object... args) {
        try {
            return MessageFormat.format(GetTranslations().get(key), args);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetTranslation (args):");
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
    public static String GetTranslationWPrefix(String key) {
        try {
            return GetTranslation("prefix") + GetTranslations().get(key);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetTranslationWPrefix:");
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
    public static String GetTranslationWPrefix(String key, Object... args) {
        try {
            return GetTranslation("prefix") + MessageFormat.format(GetTranslations().get(key), args);
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in GetTranslationWPrefix (args):");
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
    public static Component GetTranslationComp(String key) {
        try {
            return ModUtils.Literal(GetTranslations().get(key));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetTranslation:");
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
    public static Component GetTranslationComp(String key, Object... args) {
        try {
            return ModUtils.Literal(MessageFormat.format(GetTranslations().get(key), args));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetTranslation (args):");
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
    public static Component GetTranslationCompWPrefix(String key) {
        try {
            return ModUtils.Literal(GetTranslation("prefix") + GetTranslations().get(key));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetTranslationWPrefix:");
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
    public static Component GetTranslationCompWPrefix(String key, Object... args) {
        try {
            return ModUtils.Literal(GetTranslation("prefix") + MessageFormat.format(GetTranslations().get(key), args));
        } catch (Exception ex) {
            CommonClass.LOG.error("Error in Component GetTranslationWPrefix (args):");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return ModUtils.Literal("§cFailed to get the key, please report it to an admin.");
        }
    }
}

