package io.github.tavstal.portallock;

import io.github.tavstal.portallock.utils.ConfigUtils;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}