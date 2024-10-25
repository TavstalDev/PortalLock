package io.github.tavstal.portallock.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Objects;

/**
 * Utility class for handling operations related to game worlds in Minecraft.
 *
 * <p>
 * This class provides static methods for performing various tasks
 * related to Minecraft worlds, such as managing world-specific data,
 * and interacting with world entities.
 * It is designed to be used as a helper for other classes and
 * components that require world-related functionality.
 * </p>
 *
 * <p>
 * All methods in this class are static and should be accessed
 * without instantiating the class.
 * </p>
 *
 * @since 1.0
 */
public class WorldUtils {
    /**
     * Retrieves the name of the specified {@link ServerLevel}.
     *
     * <p>
     * This method provides a convenient way to access the unique name or identifier of a given
     * {@code ServerLevel} instance. Typically, this name reflects the level's dimension or environment.
     * </p>
     *
     * @param level The {@link ServerLevel} instance whose name is to be retrieved.
     * @return A {@link String} representing the name of the specified server level.
     */
    public static String GetName(ServerLevel level) {
        return level.dimension().location().toString();
    }

    /**
     * Retrieves the name of the specified {@link Level}.
     *
     * <p>
     * This method provides the unique name or identifier of a given {@code Level} instance,
     * typically representing the environment or dimension associated with the level.
     * </p>
     *
     * @param level The {@link Level} instance whose name is to be retrieved.
     * @return A {@link String} representing the name of the specified level.
     */
    public static String GetName(Level level) {
        return level.dimension().location().toString();
    }

    /**
     * Retrieves a Minecraft ServerLevel by its string name/key.
     *
     * @param server the MinecraftServer instance.
     * @param levelKey the string key/name of the level (e.g., "minecraft:overworld").
     * @return the corresponding ServerLevel, or null if not found.
     */
    public static ServerLevel GetLevelByName(MinecraftServer server, String levelKey) {
        ResourceKey<Level> levelResourceKey = null;
        for (var level : server.levelKeys()) {
            if (Objects.equals(levelKey, level.location().toString()))
            {
                levelResourceKey = level;
                break;
            }
        }

        if (levelResourceKey == null)
            return null;

        // Get the level using the resource location
        return server.getLevel(levelResourceKey);
    }
}
