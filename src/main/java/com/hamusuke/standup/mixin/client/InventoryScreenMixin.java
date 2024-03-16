package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.invoker.PlayerInvoker;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @ModifyVariable(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"), argsOnly = true)
    private static LivingEntity renderEntityInInventoryFollowsMouse(LivingEntity living) {
        if (living instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return invoker.getStand();
        }

        return living;
    }

    @ModifyVariable(method = "renderEntityInInventory", at = @At("HEAD"), argsOnly = true)
    private static LivingEntity renderEntityInInventory(LivingEntity living) {
        if (living instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return invoker.getStand();
        }

        return living;
    }
}
