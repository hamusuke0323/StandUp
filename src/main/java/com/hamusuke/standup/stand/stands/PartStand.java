package com.hamusuke.standup.stand.stands;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PartStand<Parent extends Stand> extends Stand {
    private final Parent parent;

    public PartStand(EntityType<? extends Stand> type, Parent parent) {
        super(type, parent.level(), parent.getOwner(), parent.getStandCard());
        this.parent = parent;
    }

    public Parent getParent() {
        return this.parent;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.getParent().getId());
    }

    @Override
    public boolean isAlive() {
        return this.getParent().isAlive();
    }

    @Override
    protected void onStandUp() {
    }

    @Override
    protected void onStandDown() {
    }

    @Override
    public void toggleMode(StandOperationMode mode) {
    }

    @Override
    protected void holdOwnerTick() {
    }

    @Override
    public void startHoldingOwner() {
    }

    @Override
    public void stopHoldingOwner() {
    }

    @Override
    protected void setToAI() {
    }

    @Override
    protected void setToOwner() {
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean canTakeItem(ItemStack p_21522_) {
        return false;
    }

    @Override
    public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
