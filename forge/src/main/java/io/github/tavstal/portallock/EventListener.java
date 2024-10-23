package io.github.tavstal.portallock;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {
    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        CommonClass.init(event.getServer(), false);
    }
}
