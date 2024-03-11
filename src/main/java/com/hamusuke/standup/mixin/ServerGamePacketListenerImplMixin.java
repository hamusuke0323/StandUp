package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.PlayerInvoker;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Unique
    private static boolean stillValid(AbstractContainerMenu menu, Player player) {
        if (player instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return true;
        }

        return menu.stillValid(player);
    }

    @Redirect(method = "handleRenameItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handleRenameItem(AnvilMenu instance, Player player) {
        return stillValid(instance, player);
    }

    @Redirect(method = "handleSetBeaconPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handleSetBeaconPacket(AbstractContainerMenu instance, Player player) {
        return stillValid(instance, player);
    }

    @Redirect(method = "handleSelectTrade", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/MerchantMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handleSelectTrade(MerchantMenu instance, Player p_40042_) {
        return stillValid(instance, p_40042_);
    }

    @Redirect(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handleContainerClick(AbstractContainerMenu instance, Player player) {
        return stillValid(instance, player);
    }

    @Redirect(method = "handlePlaceRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handlePlaceRecipe(AbstractContainerMenu instance, Player player) {
        return stillValid(instance, player);
    }

    @Redirect(method = "handleContainerButtonClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean handleContainerButtonClick(AbstractContainerMenu instance, Player player) {
        return stillValid(instance, player);
    }
}
