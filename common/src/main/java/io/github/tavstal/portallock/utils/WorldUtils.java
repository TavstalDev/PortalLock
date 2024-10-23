package io.github.tavstal.portallock.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class WorldUtils {
    public static String GetName(ServerLevel level) {
        return level.dimension().location().toString();
    }

    public static String GetName(Level level) {
        return level.dimension().location().toString();
    }
}
