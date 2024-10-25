package io.github.tavstal.portallock.platform.services;

import net.minecraft.server.level.ServerPlayer;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Checks if a given player has the specified permission.
     *
     * @param player The {@link ServerPlayer} whose permissions are to be checked.
     * @param permission The permission string to check, typically in dot notation (e.g., "myplugin.use").
     * @return {@code true} if the player has the specified permission; {@code false} otherwise.
     */
    boolean hasPermission(ServerPlayer player, String permission);
}