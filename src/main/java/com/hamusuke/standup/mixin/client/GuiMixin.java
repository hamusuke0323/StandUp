package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Final
    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "getCameraPlayer", at = @At("RETURN"), cancellable = true)
    private void getCameraPlayer(CallbackInfoReturnable<Player> cir) {
        if (this.minecraft.getCameraEntity() instanceof Stand stand && stand.isControlledByStandOwner()) {
            cir.setReturnValue(stand.getOwner());
        }
    }
}
