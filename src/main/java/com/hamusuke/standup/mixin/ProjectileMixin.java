package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
    protected ProjectileMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Shadow
    @Nullable
    public abstract Entity getOwner();

    @ModifyVariable(method = "shootFromRotation", at = @At("HEAD"))
    private Entity shootFromRotation$Entity(Entity entity) {
        if (this.isControllingStand()) {
            var stand = this.getStand();
            this.setPos(stand.getX(), stand.getEyeY() - 0.10000000149011612, stand.getZ());
            return stand;
        }

        return entity;
    }

    @ModifyVariable(method = "shootFromRotation", at = @At("HEAD"), ordinal = 0)
    private float shootFromRotation$XRot(float xRot) {
        if (this.isControllingStand()) {
            return this.getStand().getXRot();
        }

        return xRot;
    }

    @ModifyVariable(method = "shootFromRotation", at = @At("HEAD"), ordinal = 1)
    private float shoot(float yRot) {
        if (this.isControllingStand()) {
            return this.getStand().getYRot();
        }

        return yRot;
    }

    private Stand getStand() {
        return PlayerInvoker.invoker(this.getOwner()).getStand();
    }

    private boolean isControllingStand() {
        return this.getOwner() instanceof PlayerInvoker invoker && invoker.isControllingStand();
    }
}
