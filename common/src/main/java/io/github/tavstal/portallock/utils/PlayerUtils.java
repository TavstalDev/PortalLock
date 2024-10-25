package io.github.tavstal.portallock.utils;

import io.github.tavstal.portallock.CommonClass;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

/**
 * Utility class for handling operations related to players in Minecraft.
 *
 * <p>
 * This class provides static methods for various player-related tasks.
 * It serves as a helper for  other classes and components that require
 * player-related functionality.
 * </p>
 *
 * <p>
 * All methods in this class are static and should be accessed
 * without creating an instance of the class.
 * </p>
 *
 * @since 1.0
 */
public class PlayerUtils {

    /**
     * Determines if the specified {@link Player} is a fake player.
     *
     * <p>
     * This method evaluates the player's display name to check for the presence of
     * the player's actual name. It is based on the observation that real players
     * have their name included in the display name, while fake players (such as
     * those created by mods or plugins) often do not.
     * </p>
     *
     * <p>
     * For example, a normal player's display name contains their name, whereas
     * the display name of certain entities created by mods may not include it.
     * This method checks if the display name representation contains the player's
     * name to determine authenticity.
     * </p>
     *
     * @param player The {@link Player} instance to be checked.
     * @return {@code true} if the player is considered fake; {@code false} otherwise.
     */
    public  static boolean IsFake(Player player) {
        try
        {
            return !player.getDisplayName().toString().contains(player.getName().toString());
        }
        catch (Exception ex)
        {
            CommonClass.LOG.error("Failed to determine 'is the player fake':");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return  false;
        }
    }

    /**
     * Retrieves the {@link ServerPlayer} instance associated with the given {@link Player}.
     *
     * <p>
     * This method is used to convert a general {@link Player} instance into its
     * server-specific representation, {@link ServerPlayer}. It is particularly useful
     * when you need to access server-side functionality that is not available in the
     * general player instance.
     * </p>
     *
     * @param player The {@link Player} instance to be converted.
     * @return The corresponding {@link ServerPlayer} instance, or {@code null} if the
     *         player is not a server player or if the conversion fails.
     */
    public static ServerPlayer GetServerPlayer(Player player) {
        if (player instanceof ServerPlayer) {
            return (ServerPlayer) player; // Safe cast
        }
        return null; // If not a server player, return null or handle accordingly
    }

    /**
     * Retrieves the {@link ServerPlayer} instance associated with the given {@link Player}
     * from the specified {@link MinecraftServer}.
     *
     * <p>
     * This method is used to convert a general {@link Player} instance into its
     * server-specific representation, {@link ServerPlayer}. It is particularly useful
     * when you need to access server-side functionality that is not available in the
     * general player instance. This method requires a reference to the server to properly
     * perform the conversion.
     * </p>
     *
     * @param server The {@link MinecraftServer} instance to retrieve the player from.
     * @param player The {@link Player} instance to be converted.
     * @return The corresponding {@link ServerPlayer} instance, or {@code null} if the
     *         player is not a server player or if the conversion fails.
     */
    public static ServerPlayer GetServerPlayer(MinecraftServer server, Player player) {
        return server.getPlayerList().getPlayer(player.getUUID());
    }

    /**
     * Retrieves the {@link PlayerTeam} instance associated with the given {@link Player}.
     *
     * <p>
     * This method allows you to obtain the team information for a specified player.
     * The team information can be used to determine the player's affiliations,
     * such as team name, color, and other properties related to the team.
     * </p>
     *
     * @param player The {@link Player} instance whose team information is to be retrieved.
     * @return The {@link PlayerTeam} instance associated with the player, or {@code null}
     *         if the player is not part of any team or if the retrieval fails.
     */
    public static PlayerTeam GetPlayerTeam(Player player) {
        // Get the scoreboard for the server
        Scoreboard scoreboard = player.getScoreboard();

        // Get the player's current team (returns null if no team is assigned)
        return scoreboard.getPlayersTeam(player.getName().getString());
    }

    /**
     * Sends a title message to the specified {@link ServerPlayer}.
     *
     * <p>
     * This method displays a title and an optional subtitle on the player's screen,
     * with a specified fade-in duration. The title is typically used to convey
     * important information or notifications to the player.
     * </p>
     *
     * @param player The {@link ServerPlayer} instance to whom the title message will be sent.
     * @param mainTitle The main title text to be displayed. This can be any string
     *                  that conveys the desired message.
     * @param subTitle The subtitle text to be displayed beneath the main title.
     *                 This can be used to provide additional context or information.
     *                 Pass {@code null} or an empty string if no subtitle is needed.
     * @param fadeIn The duration (in ticks) for which the title should fade in.
     *                The default Minecraft setting is 20 ticks per second.
     */
    public static void SendTitleMessage(ServerPlayer player, String mainTitle, String subTitle, int fadeIn) {
        // Create the title component
        Component titleComponent = Component.literal(mainTitle);
        Component subtitleComponent = Component.literal(subTitle);

        // Create and send title packets
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(titleComponent);
        ClientboundSetTitleTextPacket subtitlePacket = new ClientboundSetTitleTextPacket(subtitleComponent);
        ClientboundSetTitlesAnimationPacket timingPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, 70, 20);  // Fade-in, Stay, Fade-out

        // Send packets to the player
        player.connection.send(timingPacket);
        player.connection.send(titlePacket);
        player.connection.send(subtitlePacket);
    }
}

