package io.github.tavstal.portallock;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.EAnnouncement;
import io.github.tavstal.portallock.utils.DimensionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class EventListener implements Listener {
    /**
     * Initializes the event listener by registering it with the plugin manager.
     */
    public static void init() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), PortalLock.Instance);
    }

    /**
     * Handles the ServerTickEndEvent to update dimension data periodically.
     *
     * @param event the ServerTickEndEvent
     */
    @EventHandler
    public void onServerTick(ServerTickEndEvent event) {
        var server = PortalLock.Instance.getServer();
        // Default is every 15 minutes
        if (server.getCurrentTick() % (PortalLock.Instance.getConfig().getInt("updateInterval") * 60 * 20) != 0)
            return;

        boolean shouldUpdate = false;
        for (var level : server.getWorlds()) {
            String name = level.getName();
            DimensionData dimensionData = null;
            for (var dimension : DimensionUtils.Dimensions) {
                if (Objects.equals(dimension.Key, name)) {
                    dimensionData = dimension;
                    break;
                }
            }

            if (dimensionData == null)
                continue;

            if (!dimensionData.AutoAllowByDate)
                continue;

            int dimensionIndex = DimensionUtils.Dimensions.indexOf(dimensionData);
            boolean shouldUpdateDim = false;
            if (!dimensionData.AllowEnter)
                if (Duration.between(LocalDateTime.now(), dimensionData.GetEnterDate()).getSeconds() <= 0) {
                    dimensionData.AllowEnter = true;
                    shouldUpdateDim = true;

                    dimensionData.SendMessage(null, EAnnouncement.Enter_Unlocked);
                }

            if (!dimensionData.AllowLeave)
                if (Duration.between(LocalDateTime.now(), dimensionData.GetLeaveDate()).getSeconds() <= 0) {
                    dimensionData.AllowLeave = true;
                    shouldUpdateDim = true;

                    dimensionData.SendMessage(null, EAnnouncement.Leave_Unlocked);
                }

            if (!shouldUpdateDim)
                continue;

            DimensionUtils.Dimensions.set(dimensionIndex, dimensionData);
            shouldUpdate = true;
        }

        if (shouldUpdate)
            DimensionUtils.SaveConfig();

    }

    /**
     * Handles the PlayerJoinEvent to check the player's dimension.
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerConnected(PlayerJoinEvent event) {
        DimensionUtils.checkDimension( event.getPlayer());
    }

    /**
     * Handles the PlayerPortalEvent to prevent dimension changes based on certain conditions.
     *
     * @param event the PlayerPortalEvent
     */
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo().getWorld() == null)
            return;
        if (event.getFrom().getWorld() == null)
            return;

        World fromLevel = event.getFrom().getWorld();
        World toLevel = event.getTo().getWorld();

        Player mcPlayer = event.getPlayer();
        event.setCancelled(DimensionUtils.shouldPreventDimensionChange(mcPlayer, fromLevel, toLevel));
    }
}
