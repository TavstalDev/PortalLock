package io.github.tavstal.portallock;

import io.github.tavstal.portallock.utils.PlayerUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class EventListener {
    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        CommonClass.init(event.getServer(), false);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        CommonClass.serverTick(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CommonClass.checkDimension(PlayerUtils.GetServerPlayer(player));
    }

    @SubscribeEvent
    public void onDimensionChange(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player))
            return;

        MinecraftServer server = player.getServer();
        if (server == null)
            return;

        ServerLevel fromLevel = server.getLevel(player.level().dimension());
        ServerLevel toLevel = server.getLevel(event.getDimension());

        event.setCanceled(CommonClass.shouldPreventDimensionChange(PlayerUtils.GetServerPlayer(player), fromLevel, toLevel));
    }
}
