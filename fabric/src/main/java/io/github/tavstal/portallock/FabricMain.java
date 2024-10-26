package io.github.tavstal.portallock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FabricMain implements ModInitializer {
    
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((listener) -> CommonClass.init(listener, false));

        // Server Tick
        ServerTickEvents.END_SERVER_TICK.register(CommonClass::serverTick);

        // Player Connected Event
        ServerPlayConnectionEvents.JOIN.register((handler, sender, client) ->  {
            CommonClass.checkDimension(handler.player);
        });
    }
}
