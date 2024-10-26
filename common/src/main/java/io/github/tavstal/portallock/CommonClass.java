package io.github.tavstal.portallock;

import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.ESoundType;
import io.github.tavstal.portallock.platform.Services;
import io.github.tavstal.portallock.utils.ConfigUtils;
import io.github.tavstal.portallock.utils.WorldUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Provides common utility functions and shared resources across the application.
 * <p>
 * This class serves as a central point for frequently used methods, constants, and
 * configuration access, simplifying interaction with core functionality.
 * </p>
 *
 * <p>
 * Key functionalities include:
 * <ul>
 *     <li>Configuration loading and saving.</li>
 *     <li>Log management and other utilities for application-wide usage.</li>
 * </ul>
 */
public class CommonClass {
    /** The unique identifier for the mod. */
    public static final String MOD_ID = "portallock";
    /** The display name of the mod. */
    public static final String MOD_NAME = "PortalLock";
    /** Logger instance for logging messages related to the mod. */
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Indicates whether the mod is running as a plugin.
     */
    private static boolean _isPlugin;
    /**
     * Checks if the mod is running as a plugin.
     *
     * @return {@code true} if the mod is a plugin; {@code false} otherwise.
     */
    public static boolean IsPlugin() {
        return _isPlugin;
    }

    /**
     * Holds the configuration settings for the mod.
     * This instance is initialized to {@code null} until the configuration is loaded.
     */
    private static CommonConfig _config = null;

    /**
     * Retrieves the singleton instance of {@link CommonConfig} for global configuration access.
     * <p>
     * This method provides a central point to access the configuration settings
     * across the application, ensuring a consistent state is used throughout.
     * </p>
     *
     * @return The {@link CommonConfig} instance containing current configuration values.
     */
    public static CommonConfig CONFIG() {
        if (_config == null) {
            _config = ConfigUtils.loadConfig();
            LOG.debug("Config null ? " + (_config == null));
        }
        return _config;
    }

    /**
     * Initializes the mod or plugin with the given server instance and mode.
     * <p>
     * This method sets up necessary configurations or states for the provided
     * {@code MinecraftServer} instance, depending on whether the initialization is
     * for a plugin or mod.
     * </p>
     *
     * @param server    The {@link MinecraftServer} instance to initialize, providing access
     *                  to server resources, configurations, and management functions.
     * @param isPlugin  A {@code boolean} indicating if the initialization is for a plugin
     *                  ({@code true}) or for a standalone setup ({@code false}).
     */
    public static void init(MinecraftServer server, boolean isPlugin) {
        try {
            _isPlugin = isPlugin;
            if (CONFIG().EnableDebugMode) {
                SetLogLevel("DEBUG");
            }

            // Populate dimensions
            if (CONFIG().Dimensions == null || CONFIG().Dimensions.isEmpty()) {
                List<DimensionData> dimensions = new ArrayList<DimensionData>();
                for (var level : server.getAllLevels()) {
                    String name = WorldUtils.GetName(level);
                    String displayName;
                    if (name.contains(":"))
                        displayName = name.split(":")[1];
                    else
                        displayName = name;
                    displayName = displayName.replaceAll("_", " ").toUpperCase();
                    dimensions.add(new DimensionData(name, displayName, false, "2024-01-01 00:00", "2024-01-01 00:00",
                            true, true, "minecraft:overworld",
                            true, false, name.replaceAll(":", ".") + ".enter", true, false, name.replaceAll(":", ".") + ".leave"
                            )
                    );
                }
                _config.Dimensions = dimensions;
                ConfigUtils.saveConfig(_config);
            }

            var commandDispatcher = server.getCommands().getDispatcher();
            //RespawnCommand.register(commandDispatcher);

            LOG.info(MOD_NAME + " has been loaded.");
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Failed to initialize the CommonClass:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
        }
    }

    /**
     * This method is called on each tick of the Minecraft server. It is intended
     * to execute logic that should be processed regularly while the server is running.
     *
     * <p>The method can be used to update game mechanics, handle server-side
     * events, or perform any periodic tasks that require access to the server's
     * current state.</p>
     *
     * @param server The Minecraft server instance that is currently running. This
     *               parameter provides access to the server's state, including
     *               players, worlds, and other relevant game data.
     */
    public static void serverTick(MinecraftServer server) {
        // TODO: Auto unlock
        if (server.getTickCount() % 10 != 0)
            return;
    }

    /**
     * Sets the logging level for the application.
     * <p>
     * This method configures the application's logging output based on the specified {@code level}.
     * Acceptable values may include levels like "DEBUG", "INFO", "WARN", and "ERROR",
     * depending on the logging framework used.
     * </p>
     *
     * @param level The desired logging level as a {@link String}. Valid levels are typically
     *              "DEBUG", "INFO", "WARN", "ERROR", etc.
     */
    private static void SetLogLevel(String level) {
        try {
            // Set the logging level for the logger
            LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
            LoggerConfig loggerConfig = loggerContext.getConfiguration().getLoggerConfig(LOG.getName());
            loggerConfig.setLevel(Level.valueOf(level.toUpperCase()));
            loggerContext.updateLoggers();
        }
        catch (Exception ex)
        {
            LOG.error("Failed to set the log level:");
            LOG.error(ex.getLocalizedMessage());
        }
    }


    /**
     * Determines if a player’s transition between dimensions should be prevented.
     * <p>
     * This method checks whether the specified {@code player} is allowed to change from
     * {@code oldLevel} to {@code newLevel}. A return value of {@code true} indicates
     * the transition should be disallowed, while {@code false} allows it.
     * </p>
     *
     * @param player The {@link ServerPlayer} attempting to change dimensions.
     * @param oldLevel The current {@link ServerLevel} the player is in.
     * @param newLevel The target {@link ServerLevel} the player intends to enter.
     * @return {@code true} if the dimension change should be prevented;
     *         {@code false} if the player is permitted to switch dimensions.
     */
    public static boolean shouldPreventDimensionChange(ServerPlayer player, ServerLevel oldLevel, ServerLevel newLevel) {
        try {
            DimensionData oldDimensionData = null;
            DimensionData newDimensionData = null;
            for (var dimensionData : CONFIG().Dimensions) {
                if (Objects.equals(dimensionData.Key, WorldUtils.GetName(oldLevel)))
                    oldDimensionData = dimensionData;

                if (Objects.equals(dimensionData.Key, WorldUtils.GetName(newLevel)))
                    newDimensionData = dimensionData;
            }

            var playerLevel = player.level();

            if (oldDimensionData != null) {
                if (oldDimensionData.RequireLeavePermission && Services.PLATFORM.hasPermission(player, oldDimensionData.LeavePermission)) {
                    CONFIG().LeaveFailPermission.SendToPlayer(player, oldDimensionData, false);
                    if (CONFIG().GetSoundEvent(ESoundType.FailLeave) != null)
                        playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.FailLeave), SoundSource.BLOCKS);
                    return true;
                }

                if (oldDimensionData.AutoAllowByDate && Duration.between(LocalDateTime.now(), oldDimensionData.GetLeaveDate()).getSeconds() > 0) {
                    CONFIG().LeaveFailAutoAllow.SendToPlayer(player, oldDimensionData, false);
                    if (CONFIG().GetSoundEvent(ESoundType.FailLeave) != null)
                        playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.FailLeave), SoundSource.BLOCKS);
                    return true;
                }

                if (!oldDimensionData.AllowLeave) {
                    CONFIG().LeaveFail.SendToPlayer(player, oldDimensionData, false);
                    if (CONFIG().GetSoundEvent(ESoundType.FailLeave) != null)
                        playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.FailLeave), SoundSource.BLOCKS);
                    return true;
                }
            }

            if (newDimensionData != null) {
                if (newDimensionData.RequireEnterPermission && Services.PLATFORM.hasPermission(player, newDimensionData.EnterPermission)) {
                    CONFIG().EnterFailPermission.SendToPlayer(player, newDimensionData, true);
                    if (CONFIG().GetSoundEvent(ESoundType.FailEnter) != null)
                        playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.FailEnter), SoundSource.BLOCKS);
                    return true;
                }

                if (newDimensionData.AutoAllowByDate && Duration.between(LocalDateTime.now(), newDimensionData.GetEnterDate()).getSeconds() > 0) {
                    CONFIG().EnterFailAutoAllow.SendToPlayer(player, newDimensionData, true);
                    if (CONFIG().GetSoundEvent(ESoundType.FailEnter) != null)
                        playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.FailEnter), SoundSource.BLOCKS);
                    return true;
                }

                if (!newDimensionData.AllowEnter) {
                    CONFIG().EnterFail.SendToPlayer(player, newDimensionData, true);
                    if (CONFIG().GetSoundEvent(ESoundType.FailEnter) != null)
                        playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.FailEnter), SoundSource.BLOCKS);
                    return true;
                }
            }

            if (CONFIG().GetSoundEvent(ESoundType.Success) != null)
                playerLevel.playSound(null, player.blockPosition(), CONFIG().GetSoundEvent(ESoundType.Success), SoundSource.BLOCKS);

        } catch (Exception ex) {
            LOG.error("Error in shouldPreventDimensionChange:");
            LOG.error(ex.getLocalizedMessage());
        }
        return false;
    }

    /**
     * Checks if the specified player should be in their current dimension.
     *
     * <p>This method evaluates the player's current dimension and determines
     * whether the player is permitted to remain in it based on defined rules or
     * conditions. It can be used to enforce dimension restrictions and manage
     * player transitions between dimensions.</p>
     *
     * @param player The {@link ServerPlayer} instance to check.
     */
    public static void checkDimension(ServerPlayer player) {
        try {
            DimensionData currentDimension = null;
            for (var dimensionData : CONFIG().Dimensions) {
                if (Objects.equals(dimensionData.Key, WorldUtils.GetName(player.level()))) {
                    currentDimension = dimensionData;
                    break;
                }
            }

            if (currentDimension == null)
                return;

            if (!currentDimension.KickUnauthorizedPlayers)
                return;

            if (currentDimension.RequireEnterPermission && Services.PLATFORM.hasPermission(player, currentDimension.EnterPermission)) {
                CONFIG().EnterFailPermission.SendToPlayer(player, currentDimension, true);
                kickOutOfDimension(player, currentDimension);
                return;
            }

            if (currentDimension.AutoAllowByDate && Duration.between(LocalDateTime.now(), currentDimension.GetEnterDate()).getSeconds() > 0) {
                CONFIG().EnterFailAutoAllow.SendToPlayer(player, currentDimension, true);
                kickOutOfDimension(player, currentDimension);
                return;
            }

            if (!currentDimension.AllowEnter) {
                CONFIG().EnterFail.SendToPlayer(player, currentDimension, true);
                kickOutOfDimension(player, currentDimension);
            }
        }
        catch (Exception ex) {
            LOG.error("Error in checkDimension:");
            LOG.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Kicks the specified player out of their current dimension.
     *
     * <p>This method forcibly removes the player from their current dimension,
     * typically due to violations of dimension entry rules or restrictions.
     * The player will be teleported to a predetermined location, such as their
     * spawn point or another specified dimension.</p>
     *
     * @param player The {@link ServerPlayer} instance to be kicked out.
     * @param currentDimension The {@link DimensionData} representing the current dimension of the player.
     */
    private static void kickOutOfDimension(ServerPlayer player, DimensionData currentDimension) {
        try {
            if (currentDimension.AllowKickToBed && player.getRespawnPosition() != null) {
                var respawnLoc = player.getRespawnPosition();
                var targetLevel = player.server.getLevel(player.getRespawnDimension());
                if (targetLevel != null) {
                    player.teleportTo(targetLevel, respawnLoc.getX(), respawnLoc.getY(), respawnLoc.getZ(), player.getRespawnAngle(), 0f);
                    return;
                }
            }

            ServerLevel targetLevel = WorldUtils.GetLevelByName(player.server, currentDimension.KickTargetDimension);
            if (targetLevel == null)
                return;

            var spawnLoc = targetLevel.getSharedSpawnPos();
            player.teleportTo(targetLevel, spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ(), 0f, 0f);
        }
        catch (Exception ex) {
            LOG.error("Error in kickOutOfDimension:");
            LOG.error(ex.getLocalizedMessage());
        }
    }
}