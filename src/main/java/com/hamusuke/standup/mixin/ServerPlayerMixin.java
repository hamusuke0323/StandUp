package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hamusuke.standup.registry.RegisteredItems.STAND_CARD;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerInvoker {
    protected ServerPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Override
    public boolean tryToStartFallFlying() {
        if (this.isControllingStand()) {
            return false;
        }

        return super.tryToStartFallFlying();
    }

    @Override
    public boolean canReach(BlockPos pos, double padding) {
        return (this.isControllingStand() && this.position().closerThan(pos.getCenter(), this.getStand().maxMovableDistanceFromPlayer())) || super.canReach(pos, padding);
    }

    @Override
    public boolean canReach(Entity entity, double padding) {
        return (this.isControllingStand() && this.position().closerThan(entity.position(), this.getStand().maxMovableDistanceFromPlayer())) || super.canReach(entity, padding);
    }

    @Override
    public boolean canReach(Vec3 entityHitVec, double padding) {
        return (this.isControllingStand() && this.position().closerThan(entityHitVec, this.getStand().maxMovableDistanceFromPlayer())) || super.canReach(entityHitVec, padding);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean tick(AbstractContainerMenu instance, Player player) {
        if (player instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return true;
        }

        return instance.stillValid(player);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void save(CompoundTag p_9197_, CallbackInfo ci) {
        if (!this.getStandCard().isEmpty()) {
            var card = new CompoundTag();
            this.getStandCard().save(card);
            p_9197_.put("StandCard", card);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void load(CompoundTag p_9131_, CallbackInfo ci) {
        if (p_9131_.get("StandCard") instanceof CompoundTag compoundTag) {
            var stack = ItemStack.of(compoundTag);
            if (stack.is(STAND_CARD.get())) {
                this.setStandCard(stack);
            }
        }
    }
}
