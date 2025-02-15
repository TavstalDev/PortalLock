package io.github.tavstal.portallock;

import io.github.tavstal.portallock.commands.CommandPortalLock;
import io.github.tavstal.portallock.commands.CommandPortalLockCompleter;
import io.github.tavstal.portallock.models.ESoundType;
import io.github.tavstal.portallock.utils.DimensionUtils;
import io.github.tavstal.portallock.utils.LocaleUtils;
import io.github.tavstal.portallock.utils.LoggerUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PortalLock extends JavaPlugin {
    //#region Constants
    public static final String PROJECT_NAME = "PortalLock";
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "Tavstal";
    public static final String DOWNLOAD_URL = "https://github.com/TavstalDev/PortalLock/releases/latest";
    public static final DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //#endregion
    public static PortalLock Instance;
    /**
     * Gets the plugin configuration.
     * @return The FileConfiguration object.
     */
    public static FileConfiguration GetConfig(){
        return Instance.getConfig();
    }

    /**
     * Called when the plugin is enabled.
     * Initializes the plugin, registers events and commands, loads configurations and localizations.
     */
    @Override
    public void onEnable() {
        Instance = this;
        LoggerUtils.LogInfo("Loading OpenKits...");

        // Register Events
        EventListener.init();

        // Generate config file
        saveDefaultConfig();

        // Load Localizations
        if (!LocaleUtils.Load())
        {
            LoggerUtils.LogError("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register Commands
        LoggerUtils.LogDebug("Registering commands...");
        var portalCommand = getCommand("portallock");
        if (portalCommand != null) {
            portalCommand.setExecutor(new CommandPortalLock());
            portalCommand.setTabCompleter(new CommandPortalLockCompleter());
        }

        // Load dimensions
        if (!DimensionUtils.Load()) {
            LoggerUtils.LogError("Failed to load dimensions... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Schedule a task to run every second
        LoggerUtils.LogInfo("PortalLock has been successfully loaded.");
        if (!isUpToDate())
            LoggerUtils.LogWarning("A new version of PortalLock is available! Download it at: " + DOWNLOAD_URL);
    }

    /**
     * Called when the plugin is disabled.
     * Logs an informational message indicating that the PortalLock plugin has been successfully unloaded.
     */
    @Override
    public void onDisable() {
        LoggerUtils.LogInfo("PortalLock has been successfully unloaded.");
    }

    /**
     * Reloads the plugin configuration and localizations.
     */
    public void reload() {
        LoggerUtils.LogInfo("Reloading PortalLock...");
        LoggerUtils.LogDebug("Reloading localizations...");
        LocaleUtils.Load();
        LoggerUtils.LogDebug("Localizations reloaded.");
        LoggerUtils.LogDebug("Reloading configuration...");
        this.reloadConfig();
        LoggerUtils.LogDebug("Configuration reloaded.");
        LoggerUtils.LogDebug("Reloading dimensions...");
        DimensionUtils.Load();
        LoggerUtils.LogDebug("Dimensions reloaded.");
        LoggerUtils.LogInfo("PortalLock reloaded.");
    }

    /**
     * Checks if the plugin is up to date by comparing the current version with the latest release version.
     * @return true if the plugin is up to date, false otherwise.
     */
    public boolean isUpToDate() {
        String version;
        LoggerUtils.LogDebug("Checking for updates...");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            LoggerUtils.LogDebug("Sending request to GitHub...");
            HttpGet request = new HttpGet(DOWNLOAD_URL);
            HttpResponse response = httpClient.execute(request);
            LoggerUtils.LogDebug("Received response from GitHub.");
            String jsonResponse = EntityUtils.toString(response.getEntity());
            LoggerUtils.LogDebug("Parsing response...");
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            LoggerUtils.LogDebug("Parsing release version...");
            version = jsonObject.get("tag_name").toString();
        } catch (IOException e) {
            LoggerUtils.LogError("Failed to check for updates.");
            return false;
        } catch (ParseException e) {
            LoggerUtils.LogError("Failed to parse release version.");
            return false;
        }

        LoggerUtils.LogDebug("Current version: " + VERSION);
        LoggerUtils.LogDebug("Latest version: " + version);
        return version.equalsIgnoreCase(VERSION);
    }

    /**
     * Retrieves the sound based on the specified sound type.
     *
     * @param type The type of sound to retrieve.
     * @return The corresponding Sound object, or null if the sound is "none".
     * @throws IllegalStateException if the sound type is unexpected.
     */
    public Sound getSound(ESoundType type) {
        String name = "";
        switch (type){
            case Success -> name = GetConfig().getString("SuccessSound");
            case FailEnter -> name = GetConfig().getString("FailEnterSound");
            case FailLeave -> name = GetConfig().getString("FailLeaveSound");
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        // Fixes null pointer exception
        if ("none".equalsIgnoreCase(name))
            return null;

        String key = name == null ? "minecraft:block.note_block.harp" : "minecraft:" + name;
        return Sound.sound(Key.key(key), Sound.Source.PLAYER, 1.0f, 1.0f);
    }
}
