package io.github.tavstal.portallock;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public class EventListener {
    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        CommonClass.init(event.getServer(), false);
    }

}
