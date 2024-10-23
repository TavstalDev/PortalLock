package io.github.tavstal.portallock;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(CommonClass.MOD_ID)
public class ForgeMain {

    public ForgeMain() {
        // Use Forge to bootstrap the Common mod.
        MinecraftForge.EVENT_BUS.register(new EventListener());
    }
}