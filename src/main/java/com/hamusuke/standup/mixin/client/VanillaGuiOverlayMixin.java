package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.stand.Stand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VanillaGuiOverlay.class)
public abstract class VanillaGuiOverlayMixin {
    @Redirect(method = {"lambda$static$8", "lambda$static$9", "lambda$static$10", "lambda$static$11"}, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;shouldDrawSurvivalElements()Z"))
    private static boolean render(ForgeGui instance) {
        return instance.getMinecraft().gameMode.canHurtPlayer() && ((instance.getMinecraft().getCameraEntity() instanceof Stand stand && stand.isControlledByStandOwner()) || instance.getMinecraft().getCameraEntity() instanceof Player);
    }
}
