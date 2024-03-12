package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin {
    @Redirect(method = "renderHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity renderHealth(Minecraft instance) {
        var cam = instance.getCameraEntity();
        return cam instanceof Stand stand && stand.isControlledByStandOwner() ? stand.getOwner() : cam;
    }

    @Redirect(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity renderFood(Minecraft instance) {
        var cam = instance.getCameraEntity();
        return cam instanceof Stand stand && stand.isControlledByStandOwner() ? stand.getOwner() : cam;
    }

    @Redirect(method = "renderAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"))
    private Entity renderAir(Minecraft instance) {
        var cam = instance.getCameraEntity();
        return cam instanceof Stand stand && stand.isControlledByStandOwner() ? stand.getOwner() : cam;
    }
}
