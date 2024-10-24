package io.github.tavstal.portallock;

import io.github.tavstal.portallock.utils.ModUtils;
import io.github.tavstal.portallock.utils.PlayerUtils;
import io.github.tavstal.portallock.utils.WorldUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {
    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        CommonClass.init(event.getServer(), false);
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        MinecraftServer server = player.getServer();
        if (server == null)
            return;

        ServerLevel fromLevel = server.getLevel(event.getFrom());
        ServerLevel toLevel = server.getLevel(event.getTo());

        event.setCanceled(!CommonClass.AttemptDimensionChange(PlayerUtils.GetServerPlayer(player), fromLevel, toLevel));
    }
}
