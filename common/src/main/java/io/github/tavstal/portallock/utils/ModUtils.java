package io.github.tavstal.portallock.utils;

import com.mojang.brigadier.LiteralMessage;
import io.github.tavstal.portallock.CommonClass;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Scoreboard;

import java.text.MessageFormat;

/**
 * Utility class for managing operations related to Minecraft mods.
 *
 * <p>
 * This class provides static methods to assist with various mod-related
 * functionalities.
 * It serves as a centralized location for commonly used operations
 * to promote code reuse and maintainability.
 * </p>
 *
 * <p>
 * All methods in this class are static and can be accessed without
 * creating an instance of the class.
 * </p>
 *
 * @since 1.0
 */
public class ModUtils {
    /**
     * Creates a {@link Component} from the specified text.
     *
     * <p>
     * This method converts a string into a literal {@link Component},
     * which can be used for sending messages or displaying text in the game.
     * The resulting component can include styling and formatting based
     * on the Minecraft chat component system.
     * </p>
     *
     * @param text The text to be converted into a {@link Component}.
     *              This should be a plain string that represents the literal message.
     * @return A {@link Component} representing the given text,
     *         suitable for use in chat messages and other UI elements.
     */
    public static Component Literal(String text) {
        return net.minecraft.network.chat.ComponentUtils.fromMessage(new LiteralMessage(text));
    }

    /**
     * Broadcasts a message to all players.
     *
     * <p>
     * This method sends the given text as a chat message to all players.
     * It can be used to notify players of important events or information
     * related to the entity.
     * </p>
     *
     * @param entity The {@link Entity} from which the broadcast will originate.
     *               The message will be sent to all players.
     * @param text   The message text to be broadcasted. This should be a string representing
     *               the message content that players will receive.
     */
    public static void BroadcastMessage(Entity entity, String text) {
        var server = entity.getServer();
        if (server == null)
        {
            CommonClass.LOG.error("BroadcastMessage 1 -> Failed to get the server.");
            return;
        }

        var messageComponent = Literal(text);
        // Send Message to the server
        server.sendSystemMessage(messageComponent);
        // Send Message to all clients
        for (var player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(messageComponent);
        }
    }

    /**
     * Broadcasts a formatted message to all players.
     *
     * <p>
     * This method sends the given text as a chat message to all players.
     * It formats the message using the specified arguments, sending
     * it both as a system message to the server and individually to all players connected to
     * the server. This can be used to notify players of important events or information
     * related to the entity, allowing for dynamic content in the message.
     * </p>
     *
     * @param entity The {@link Entity} from which the broadcast will originate.
     *               The message will be sent to all players.
     * @param text   The message text to be broadcasted, which can include placeholders for
     *               dynamic content (e.g., "{0}", "{1}").
     * @param args   The arguments to be used for formatting the message text. The message
     *               will be formatted using {@link MessageFormat#format(String, Object...)}.
     *
     * @throws IllegalStateException If the server cannot be retrieved from the entity.
     */
    public static void BroadcastMessage(Entity entity, String text, Object ... args) {
        var server = entity.getServer();
        if (server == null)
        {
            CommonClass.LOG.error("BroadcastMessage 2 -> Failed to get the server.");
            return;
        }

        var messageComponent = Literal(MessageFormat.format(text, args));
        // Send Message to the server
        server.sendSystemMessage(messageComponent);
        // Send Message to all clients
        for (var player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(messageComponent);
        }
    }

    /**
     * Broadcasts a message to all players in a specified world.
     *
     * <p>
     * This method sends the given text as a chat message to all players in the world
     * identified by the provided world key. It retrieves the target world from the
     * server using the specified key and sends the message both as a system message
     * to the server and individually to all players connected to that world.
     * </p>
     *
     * @param entity  The {@link Entity} from which the broadcast will originate.
     *                This entity is used to retrieve the server context.
     * @param text    The message text to be broadcasted to players in the specified world.
     * @param worldKey The key that identifies the target world to which the message will be sent.
     *
     * @throws IllegalArgumentException If the specified world key does not correspond to a valid world.
     * @throws IllegalStateException If the server cannot be retrieved from the entity or if there are no players in the target world.
     */
    public static void BroadcastMessageByWorld(Entity entity, String text, String worldKey) {
        var server = entity.getServer();
        if (server == null)
        {
            CommonClass.LOG.error("BroadcastMessageByWorld 1 -> Failed to get the server.");
            return;
        }

        var messageComponent = Literal(text);
        // Send Message to the server
        server.sendSystemMessage(messageComponent);
        // Send Message to all clients
        for (var player : server.getPlayerList().getPlayers()) {
            if (WorldUtils.GetName(EntityUtils.GetLevel(player)).equals(worldKey))
                player.sendSystemMessage(messageComponent);
        }
    }

    /**
     * Broadcasts a formatted message to all players in a specified world.
     *
     * <p>
     * This method sends the given text, formatted with the provided arguments, as a chat message
     * to all players in the world identified by the specified world key. It retrieves the target
     * world from the server using the given key and sends the message both as a system message
     * to the server and individually to all players connected to that world.
     * </p>
     *
     * @param entity   The {@link Entity} from which the broadcast will originate.
     *                 This entity is used to retrieve the server context.
     * @param text     The message text to be formatted and broadcasted to players in the specified world.
     * @param worldKey The key that identifies the target world to which the message will be sent.
     * @param args     The arguments to format the message text, following the
     *                 {@link MessageFormat} style.
     *
     * @throws IllegalArgumentException If the specified world key does not correspond to a valid world.
     * @throws IllegalStateException If the server cannot be retrieved from the entity or if there are no players in the target world.
     */
    public static void BroadcastMessageByWorld(Entity entity, String text, String worldKey, Object ... args) {
        var server = entity.getServer();
        if (server == null)
        {
            CommonClass.LOG.error("BroadcastMessageByWorld 2 -> Failed to get the server.");
            return;
        }

        var messageComponent = Literal(MessageFormat.format(text, args));
        // Send Message to the server
        server.sendSystemMessage(messageComponent);
        // Send Message to all clients
        for (var player : server.getPlayerList().getPlayers()) {
            if (WorldUtils.GetName(EntityUtils.GetLevel(player)).equals(worldKey))
                player.sendSystemMessage(messageComponent);
        }
    }

    /**
     * Retrieves the server's scoreboard instance.
     *
     * <p>
     * This method fetches the scoreboard associated with the specified
     * Minecraft server. The scoreboard can be used for tracking and
     * managing scores for players and objectives within the game.
     * It is useful for implementing features like custom game rules,
     * player stats, or other competitive elements.
     * </p>
     *
     * @param server The {@link MinecraftServer} instance from which to retrieve the scoreboard.
     * @return The {@link Scoreboard} associated with the given server.
     * @throws IllegalArgumentException If the provided server instance is null.
     */
    public static Scoreboard getServerScoreboard(MinecraftServer server) {
        try {
            // Get the main overworld level (you can adjust this if needed for other dimensions)
            ServerLevel overworld = server.getLevel(ServerLevel.OVERWORLD);

            if (overworld != null) {
                return overworld.getScoreboard();
            }
            return  null;
        } catch (Exception e) {
            CommonClass.LOG.error("Failed to get server scoreboard.");
            CommonClass.LOG.error(e.getLocalizedMessage());
            return null;  // Or handle in a version-specific way
        }
    }
}
