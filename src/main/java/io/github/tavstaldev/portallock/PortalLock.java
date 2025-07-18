package io.github.tavstaldev.portallock;

import io.github.tavstaldev.minecorelib.PluginBase;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.portallock.commands.CommandPortalLock;
import io.github.tavstaldev.portallock.commands.CommandPortalLockCompleter;
import io.github.tavstaldev.portallock.models.ESoundType;
import io.github.tavstaldev.portallock.utils.DimensionUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.intellij.lang.annotations.Subst;

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
        _logger.Info("Loading RespawnTimer...");

        // Register Events
        EventListener.init();

        // Generate config file
        saveDefaultConfig();

        // Load Localizations
        if (!getTranslator().Load())
        {
            _logger.Error("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register Commands
        _logger.Debug("Registering commands...");
        var portalCommand = getCommand("portallock");
        if (portalCommand != null) {
            portalCommand.setExecutor(new CommandPortalLock());
            portalCommand.setTabCompleter(new CommandPortalLockCompleter());
        }

        // Load dimensions
        if (!DimensionUtils.Load()) {
            _logger.Error("Failed to load dimensions... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Schedule a task to run every second
        _logger.Ok("PortalLock has been successfully loaded.");
        isUpToDate().thenAccept(upToDate -> {
            if (upToDate) {
                _logger.Ok("Plugin is up to date!");
            } else {
                _logger.Warn("A new version of the plugin is available: " + getDownloadUrl());
            }
        }).exceptionally(e -> {
            _logger.Error("Failed to determine update status: " + e.getMessage());
            return null;
        });
    }

    /**
     * Called when the plugin is disabled.
     * Logs an informational message indicating that the PortalLock plugin has been successfully unloaded.
     */
    @Override
    public void onDisable() {
        _logger.Info("PortalLock has been successfully unloaded.");
    }

    /**
     * Reloads the plugin configuration and localizations.
     */
    public void reload() {
        _logger.Info("Reloading PortalLock...");
        _logger.Debug("Reloading localizations...");
        getTranslator().Load();
        _logger.Debug("Localizations reloaded.");
        _logger.Debug("Reloading configuration...");
        this.reloadConfig();
        _logger.Debug("Configuration reloaded.");
        _logger.Debug("Reloading dimensions...");
        DimensionUtils.Load();
        _logger.Debug("Dimensions reloaded.");
        _logger.Info("PortalLock reloaded.");
    }

    /**
     * Retrieves the sound based on the specified sound type.
     *
     * @param type The type of sound to retrieve.
     * @return The corresponding Sound object, or null if the sound is "none".
     * @throws IllegalStateException if the sound type is unexpected.
     */
    public Sound getSound(ESoundType type) {
        @Subst("") String name;
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
