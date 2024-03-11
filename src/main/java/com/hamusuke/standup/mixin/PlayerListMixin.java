package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.PlayerInvoker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Redirect(method = "broadcast", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getX()D"))
    private double broadcast$getX(ServerPlayer instance) {
        if (instance instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return invoker.getStand().getX();
        }

        return instance.getX();
    }

    @Redirect(method = "broadcast", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getY()D"))
    private double broadcast$getY(ServerPlayer instance) {
        if (instance instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return invoker.getStand().getY();
        }

        return instance.getY();
    }

    @Redirect(method = "broadcast", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getZ()D"))
    private double broadcast$getZ(ServerPlayer instance) {
        if (instance instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return invoker.getStand().getZ();
        }

        return instance.getZ();
    }
}
