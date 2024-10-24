package io.github.tavstal.portallock.mixin;

import io.github.tavstal.portallock.CommonClass;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "changeDimension(Lnet/minecraft/world/level/portal/DimensionTransition;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD"), cancellable = true)
    private void injected(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir){
        ServerPlayer serverPlayer = (ServerPlayer)(Object)this;
        if (!CommonClass.AttemptDimensionChange(serverPlayer, serverPlayer.serverLevel(), dimensionTransition.newLevel())) {
            cir.setReturnValue(null);
        }
    }
}
