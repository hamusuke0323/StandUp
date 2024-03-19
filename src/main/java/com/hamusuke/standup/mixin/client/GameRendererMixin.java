package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @ModifyVariable(method = "tickFov", at = @At(value = "STORE"))
    private float tickFov(float f) {
        if (this.minecraft.getCameraEntity() instanceof Stand stand && stand.canSprint() && stand.getOwner() instanceof AbstractClientPlayer player) {
            f = player.getFieldOfViewModifier();
        }

        return f;
    }
}
