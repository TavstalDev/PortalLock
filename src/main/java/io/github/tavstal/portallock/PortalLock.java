package io.github.tavstal.portallock;

import io.github.tavstal.minecorelib.PluginBase;
import io.github.tavstal.minecorelib.core.PluginLogger;
import io.github.tavstal.minecorelib.core.PluginTranslator;
import io.github.tavstal.portallock.commands.CommandPortalLock;
import io.github.tavstal.portallock.commands.CommandPortalLockCompleter;
import io.github.tavstal.portallock.models.ESoundType;
import io.github.tavstal.portallock.utils.DimensionUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PortalLock extends PluginBase {
    public static final DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static PortalLock Instance;
    public static PluginLogger Logger() {
        return Instance.getCustomLogger();
    }
    public static PluginTranslator Translator() {
        return Instance.getTranslator();
    }
    /**
     * Gets the plugin configuration.
     * @return The FileConfiguration object.
     */
    public static FileConfiguration GetConfig(){
        return Instance.getConfig();
    }

    public PortalLock() {
        super("PortalLock",
                "1.0.0",
                "Tavstal",
                "https://github.com/TavstalDev/PortalLock/releases/latest",
                new String[]{"eng", "hun"}
        );
    }

    /**
     * Called when the plugin is enabled.
     * Initializes the plugin, registers events and commands, loads configurations and localizations.
     */
    @Override
    public void onEnable() {
        Instance = this;
        getCustomLogger().Info("Loading RespawnTimer...");

        // Register Events
        EventListener.init();

        // Generate config file
        saveDefaultConfig();

        // Load Localizations
        if (!getTranslator().Load())
        {
            getCustomLogger().Error("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register Commands
        getCustomLogger().Debug("Registering commands...");
        var portalCommand = getCommand("portallock");
        if (portalCommand != null) {
            portalCommand.setExecutor(new CommandPortalLock());
            portalCommand.setTabCompleter(new CommandPortalLockCompleter());
        }

        // Load dimensions
        if (!DimensionUtils.Load()) {
            getCustomLogger().Error("Failed to load dimensions... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Schedule a task to run every second
        getCustomLogger().Info("PortalLock has been successfully loaded.");
        if (!isUpToDate())
            getCustomLogger().Warn("A new version of PortalLock is available! Download it at: " + getDownloadUrl());
    }

    /**
     * Called when the plugin is disabled.
     * Logs an informational message indicating that the PortalLock plugin has been successfully unloaded.
     */
    @Override
    public void onDisable() {
        getCustomLogger().Info("PortalLock has been successfully unloaded.");
    }

    /**
     * Reloads the plugin configuration and localizations.
     */
    public void reload() {
        getCustomLogger().Info("Reloading PortalLock...");
        getCustomLogger().Debug("Reloading localizations...");
        getTranslator().Load();
        getCustomLogger().Debug("Localizations reloaded.");
        getCustomLogger().Debug("Reloading configuration...");
        this.reloadConfig();
        getCustomLogger().Debug("Configuration reloaded.");
        getCustomLogger().Debug("Reloading dimensions...");
        DimensionUtils.Load();
        getCustomLogger().Debug("Dimensions reloaded.");
        getCustomLogger().Info("PortalLock reloaded.");
    }

    /**
     * Checks if the plugin is up to date by comparing the current version with the latest release version.
     * @return true if the plugin is up to date, false otherwise.
     */
    public boolean isUpToDate() {
        String version;
        getCustomLogger().Debug("Checking for updates...");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            getCustomLogger().Debug("Sending request to GitHub...");
            HttpGet request = new HttpGet(getDownloadUrl());
            HttpResponse response = httpClient.execute(request);
            getCustomLogger().Debug("Received response from GitHub.");
            String jsonResponse = EntityUtils.toString(response.getEntity());
            getCustomLogger().Debug("Parsing response...");
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            getCustomLogger().Debug("Parsing release version...");
            version = jsonObject.get("tag_name").toString();
        } catch (IOException e) {
            getCustomLogger().Error("Failed to check for updates.");
            return false;
        } catch (ParseException e) {
            getCustomLogger().Error("Failed to parse release version.");
            return false;
        }

        getCustomLogger().Debug("Current version: " + getVersion());
        getCustomLogger().Debug("Latest version: " + version);
        return version.equalsIgnoreCase(getVersion());
    }

    /**
     * Retrieves the sound based on the specified sound type.
     *
     * @param type The type of sound to retrieve.
     * @return The corresponding Sound object, or null if the sound is "none".
     * @throws IllegalStateException if the sound type is unexpected.
     */
    public Sound getSound(ESoundType type) {
        String name;
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
