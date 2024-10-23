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

public class PlayerUtils {
    public  static boolean IsFake(Player player) {
        try
        {
            // This might look silly, but here is an explanation using Create as example.
            // This is how a normal player's DisplayName looks:
            // # START
            // literal{}[style={clickEvent=ClickEvent{action=SUGGEST_COMMAND, value='/tell Tavstal '},
            // hoverEvent=HoverEvent{action=<action show_entity>,
            // value='net.minecraft.network.chat.HoverEvent$EntityTooltipInfo@e6e072c2'},insertion=Tavstal},
            // siblings=[empty[style={color=white}, siblings=[empty[style={}], literal{Tavstal},
            // literal{ [overworld] }[style={}]]]]]
            // # END
            // This is how Create's Deployer's DisplayName looks:
            // # translation{key='create.block.deployer.damage_source_name', args=[]}
            // Because player.getName() (literal{Tavstal}) is the same on both and only the real player's DisplayName contains it.
            // So we can use it to determine the Player object is real or fake.
            return !player.getDisplayName().toString().contains(player.getName().toString());
        }
        catch (Exception ex)
        {
            CommonClass.LOG.error("Failed to determine 'is the player fake':");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return  false;
        }
    }

    public static ServerPlayer GetServerPlayer(Player player) {
        if (player instanceof ServerPlayer) {
            return (ServerPlayer) player; // Safe cast
        }
        return null; // If not a server player, return null or handle accordingly
    }

    public static ServerPlayer GetServerPlayer(MinecraftServer server, Player player) {
        return server.getPlayerList().getPlayer(player.getUUID());
    }

    public static PlayerTeam GetPlayerTeam(Player player) {
        // Get the scoreboard for the server
        Scoreboard scoreboard = player.getScoreboard();

        // Get the player's current team (returns null if no team is assigned)
        return scoreboard.getPlayersTeam(player.getName().getString());
    }

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

