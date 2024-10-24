package io.github.tavstal.portallock;

import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.utils.ConfigUtils;
import io.github.tavstal.portallock.utils.WorldUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommonClass {
    public static final String MOD_ID = "portallock";
    public static final String MOD_NAME = "PortalLock";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    private static boolean _isPlugin;
    public static boolean IsPlugin() {
        return _isPlugin;
    }

    private static CommonConfig _config = null;

    public static CommonConfig CONFIG() {
        if (_config == null) {
            _config = ConfigUtils.LoadConfig();
            LOG.debug("Config null ? " + (_config == null));
        }
        return _config;
    }

    public static void init(MinecraftServer server, boolean isPlugin) {
        try {
            _isPlugin = isPlugin;
            if (CONFIG().EnableDebugMode) {
                SetLogLevel("DEBUG");
            }

            // Populate dimensions
            if (CONFIG().Dimensions == null || CONFIG().Dimensions.isEmpty()) {
                List<DimensionData> dimensions = new ArrayList<>();
                for (var level : server.getAllLevels()) {
                    String name = WorldUtils.GetName(level);
                    String displayName;
                    if (name.contains(":"))
                        displayName = name.split(":")[1];
                    else
                        displayName = name;
                    displayName = displayName.replaceAll("_", " ").toUpperCase();
                    dimensions.add(new DimensionData(name, displayName, false, LocalDate.now().toString(), LocalDate.now().toString(),
                            true, true, "minecraft:overworld",
                            true, false, name + ".enter", true, false, name + ".leave"
                            )
                    );
                }
                _config.Dimensions = dimensions;
                ConfigUtils.SaveConfig(_config);
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

    // true - allow, false - disallow
    public static boolean AttemptDimensionChange(ServerPlayer player, ServerLevel oldLevel, ServerLevel newLevel) {

        DimensionData oldDimensionData = null;
        DimensionData newDimensionData = null;
        for (var dimensionData : CONFIG().Dimensions) {
            if (Objects.equals(dimensionData.Key, WorldUtils.GetName(oldLevel)))
                oldDimensionData = dimensionData;

            if (Objects.equals(dimensionData.Key, WorldUtils.GetName(newLevel)))
                newDimensionData = dimensionData;
        }

        if (oldDimensionData != null) {
            // TODO
            if (oldDimensionData.RequireLeavePermission && ) {
                CONFIG().LeaveFailPermission.SendToPlayer(player, oldDimensionData, false);
                return false;
            }

            if (oldDimensionData.AutoAllowByDate && Duration.between(LocalDateTime.now(), oldDimensionData.GetLeaveDate()).getSeconds() > 0) {
                CONFIG().LeaveFailAutoAllow.SendToPlayer(player, oldDimensionData, false);
                return  false;
            }

            if (!oldDimensionData.AllowLeave) {
                CONFIG().LeaveFail.SendToPlayer(player, oldDimensionData, false);
                return false;
            }
        }

        if (newDimensionData != null) {

        }

        return true;
    }
}