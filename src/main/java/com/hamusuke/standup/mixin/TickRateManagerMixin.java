package com.hamusuke.standup.mixin;

import com.hamusuke.standup.stand.Stand;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickRateManager.class)
public abstract class TickRateManagerMixin {
    @Shadow
    public abstract boolean isEntityFrozen(Entity p_311574_);

    @Inject(method = "isEntityFrozen", at = @At("RETURN"), cancellable = true)
    private void isEntityFrozen(Entity p_311574_, CallbackInfoReturnable<Boolean> cir) {
        if (p_311574_ instanceof Stand stand && stand.getOwner() != null && !this.isEntityFrozen(stand.getOwner())) {
            cir.setReturnValue(false);
        }
    }
}
