package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

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

    @Redirect(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
    private EntityHitResult pick(Entity entity, Vec3 vec3, Vec3 vec32, AABB aabb, Predicate<Entity> predicate, double d1) {
        var res = ProjectileUtil.getEntityHitResult(entity, vec3, vec32, aabb, predicate, d1);
        if (res == null && entity instanceof PlayerInvoker invoker && !invoker.isControllingStand() && invoker.isStandAlive()) {
            res = ProjectileUtil.getEntityHitResult(entity, vec3, vec32, aabb, entity1 -> entity1 instanceof Stand stand && stand.getOwner() == entity, d1);
        }

        return res;
    }
}
