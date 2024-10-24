package io.github.tavstal.portallock;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        CommonClass.init(((CraftServer)this.getServer()).getServer(), true);
        this.getServer().getPluginManager().registerEvents(this, this);
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
        event.setCancelled(!CommonClass.AttemptDimensionChange(mcPlayer, fromLevel, toLevel));
    }
}
