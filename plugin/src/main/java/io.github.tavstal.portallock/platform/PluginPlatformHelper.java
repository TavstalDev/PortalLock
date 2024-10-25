package io.github.tavstal.portallock.platform;

import io.github.tavstal.portallock.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class PluginPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Paper";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return false;
    }

    /*@Override
    public boolean isPlugin() {
        return true;
    }*/

    @Override
    public boolean isDevelopmentEnvironment() {

        return false;
    }

    @Override
    public boolean hasPermission(ServerPlayer player, String permission) {
        CraftPlayer craftPlayer = player.getBukkitEntity();
        return  craftPlayer.hasPermission(permission);
    }
}
