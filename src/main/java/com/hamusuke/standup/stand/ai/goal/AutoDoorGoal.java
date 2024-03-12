package com.hamusuke.standup.stand.ai.goal;

import com.hamusuke.standup.CommonConfig;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.DoorBlock;

public class AutoDoorGoal extends Goal {
    protected final Stand stand;
    protected BlockPos doorPos;
    protected boolean hasDoor;
    private boolean passed;
    private float doorOpenDirX;
    private float doorOpenDirZ;
    private final boolean closeDoor;
    private int forgetTime;

    public AutoDoorGoal(Stand stand) {
        this(stand, true);
    }

    public AutoDoorGoal(Stand stand, boolean closeDoor) {
        this.doorPos = BlockPos.ZERO;
        this.stand = stand;
        this.closeDoor = closeDoor;
    }

    protected boolean isOpen() {
        if (!this.hasDoor) {
            return false;
        } else {
            var blockState = this.stand.level().getBlockState(this.doorPos);
            if (!(blockState.getBlock() instanceof DoorBlock)) {
                this.hasDoor = false;
                return false;
            } else {
                return blockState.getValue(DoorBlock.OPEN);
            }
        }
    }

    protected void setOpen(boolean p_25196_) {
        if (this.hasDoor && CommonConfig.standCanOpenDoor) {
            var blockState = this.stand.level().getBlockState(this.doorPos);
            if (blockState.getBlock() instanceof DoorBlock door) {
                door.setOpen(this.stand, this.stand.level(), blockState, this.doorPos, p_25196_);
            }
        }
    }

    protected Direction getDoorDirection(BlockPos pos) {
        var state = this.stand.level().getBlockState(pos);
        return state.getBlock() instanceof DoorBlock ? state.getValue(DoorBlock.FACING) : Direction.UP;
    }

    protected boolean isInFrontOfDoor() {
        var owner = this.stand.getOwner();
        this.doorPos = owner.blockPosition().above();
        this.hasDoor = DoorBlock.isWoodenDoor(this.stand.level(), this.doorPos) && this.getDoorDirection(this.doorPos).getOpposite() == owner.getDirection();

        var pos = owner.blockPosition().relative(owner.getDirection()).above();
        if (!this.hasDoor && this.getDoorDirection(pos) == owner.getDirection()) {
            this.doorPos = pos;
            this.hasDoor = DoorBlock.isWoodenDoor(this.stand.level(), this.doorPos);
        }

        return this.hasDoor;
    }

    @Override
    public boolean canUse() {
        return CommonConfig.standCanOpenDoor && !this.stand.isAggressive() && this.isInFrontOfDoor();
    }

    @Override
    public boolean canContinueToUse() {
        return this.closeDoor && this.forgetTime > 0 && !this.passed;
    }

    @Override
    public void start() {
        this.passed = false;
        this.doorOpenDirX = (float) (this.doorPos.getX() + 0.5D - this.stand.getX());
        this.doorOpenDirZ = (float) (this.doorPos.getZ() + 0.5D - this.stand.getZ());

        this.forgetTime = 20;

        boolean hasOpened = this.isOpen();
        if (!hasOpened) {
            this.setOpen(true);
            this.stand.swing(InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public void stop() {
        boolean hasOpened = this.isOpen();
        if (hasOpened) {
            this.setOpen(false);
            this.stand.swing(InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.forgetTime--;

        float x = (float) (this.doorPos.getX() + 0.5D - this.stand.getX());
        float z = (float) (this.doorPos.getZ() + 0.5D - this.stand.getZ());
        float dir = this.doorOpenDirX * x + this.doorOpenDirZ * z;
        if (dir < 0.0F) {
            this.passed = true;
        }
    }
}
