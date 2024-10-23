package io.github.tavstal.portallock;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        CommonClass.init(((CraftServer)this.getServer()).getServer(), true);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onServerTick(ServerTickEndEvent event) {


    }
}
