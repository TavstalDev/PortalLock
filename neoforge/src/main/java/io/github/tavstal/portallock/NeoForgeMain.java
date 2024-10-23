package io.github.tavstal.portallock;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CommonClass.MOD_ID)
public class NeoForgeMain {

    public NeoForgeMain(IEventBus eventBus) {
        // Use Forge to bootstrap the Common mod.
        NeoForge.EVENT_BUS.register(new EventListener());
    }
}