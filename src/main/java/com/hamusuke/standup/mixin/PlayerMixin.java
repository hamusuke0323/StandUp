package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.s2c.StandAppearNotify;
import com.hamusuke.standup.network.packet.s2c.StandCardSetNotify;
import com.hamusuke.standup.network.packet.s2c.StandDisappearNotify;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hamusuke.standup.registry.RegisteredItems.STAND_CARD;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerInvoker {
    @Unique
    @Nullable
    protected Stand stand;
    @Unique
    protected ItemStack standCard = ItemStack.EMPTY;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Override
    public @Nullable Stand getStand() {
        return this.stand;
    }

    @Override
    public void standUp(@NotNull Stand stand) {
        if (this.stand != null) {
            this.stand.remove(RemovalReason.KILLED);
        }

        this.stand = stand;
        if (!this.level().isClientSide && this.stand.getOwner() != null) {
            NetworkManager.sendToClient(new StandAppearNotify(this.stand.getOwner().getId(), this.stand.getId()), (ServerPlayer) (Object) this);
        }
    }

    @Override
    public void standDown() {
        if (this.isStandAlive()) {
            this.stand.remove(RemovalReason.KILLED);
        }

        var owner = this.stand.getOwner();
        this.stand = null;

        if (!this.level().isClientSide && owner != null) {
            NetworkManager.sendToClient(new StandDisappearNotify(owner.getId()), (ServerPlayer) owner);
        }
    }

    @Override
    public ItemStack getStandCard() {
        if (this.standCard == null) {
            this.standCard = ItemStack.EMPTY;
        }

        return this.standCard;
    }

    @Override
    public void setStandCard(ItemStack standCard) {
        this.standCard = standCard == null || !standCard.is(STAND_CARD.get()) ? ItemStack.EMPTY : standCard;

        if (!this.level().isClientSide) {
            NetworkManager.sendToClient(new StandCardSetNotify((Player) (Object) this, this.standCard), (ServerPlayer) (Object) this);
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attack(Entity p_36347_, CallbackInfo ci) {
        if (this.isControllingStand()) {
            this.stand.doHurtTarget(p_36347_);
            ci.cancel();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    private boolean tick(AbstractContainerMenu instance, Player player) {
        if (player instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            return true;
        }

        return instance.stillValid(player);
    }

    @Override
    public boolean isPickable() {
        return !this.isControllingStand() && super.isPickable();
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isControllingStand() || super.canBeHitByProjectile();
    }
}
