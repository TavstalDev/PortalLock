package io.github.tavstal.portallock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricMain implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((listener) -> CommonClass.init(listener, false));
    }
}
