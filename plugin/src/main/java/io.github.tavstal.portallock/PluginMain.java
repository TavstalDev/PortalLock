package io.github.tavstal.portallock;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        CommonClass.init(((CraftServer)this.getServer()).getServer(), true);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerConnected(PlayerJoinEvent event) {
        ServerPlayer mcPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        CommonClass.checkDimension(mcPlayer);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo().getWorld() == null)
            return;
        if (event.getFrom().getWorld() == null)
            return;

        ServerLevel fromLevel = ((CraftWorld)event.getFrom().getWorld()).getHandle();
        ServerLevel toLevel = ((CraftWorld)event.getTo().getWorld()).getHandle();

        ServerPlayer mcPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        event.setCancelled(CommonClass.shouldPreventDimensionChange(mcPlayer, fromLevel, toLevel));
    }
}
