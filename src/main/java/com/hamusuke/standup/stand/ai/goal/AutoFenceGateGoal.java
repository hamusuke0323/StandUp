package com.hamusuke.standup.stand.ai.goal;

import com.hamusuke.standup.CommonConfig;
import com.hamusuke.standup.invoker.FenceGateBlockInvoker;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class AutoFenceGateGoal extends Goal {
    protected final Stand stand;
    protected final FenceGate above = new AboveFenceGate();
    protected final FenceGate below = new FenceGate();
    private boolean passed;
    private float gateOpenDirX;
    private float gateOpenDirZ;
    private final boolean closeGate;
    private int forgetTime;

    public AutoFenceGateGoal(Stand stand) {
        this(stand, true);
    }

    public AutoFenceGateGoal(Stand stand, boolean closeGate) {
        this.stand = stand;
        this.closeGate = closeGate;
    }

    protected boolean isInFrontOfGate() {
        return this.above.isInFrontOfGate() | this.below.isInFrontOfGate();
    }

    @Override
    public boolean canUse() {
        return CommonConfig.standCanOpenGate && !this.stand.isAggressive() && this.isInFrontOfGate();
    }

    @Override
    public boolean canContinueToUse() {
        return this.closeGate && this.forgetTime > 0 && !this.passed;
    }

    @Override
    public void start() {
        this.passed = false;
        this.gateOpenDirX = (float) (this.getPrimaryGate().gatePos.getX() + 0.5D - this.stand.getX());
        this.gateOpenDirZ = (float) (this.getPrimaryGate().gatePos.getZ() + 0.5D - this.stand.getZ());

        this.forgetTime = 20;

        this.above.open();
        this.below.open();
    }

    @Override
    public void stop() {
        this.above.close();
        this.below.close();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.forgetTime--;

        float x = (float) (this.getPrimaryGate().gatePos.getX() + 0.5D - this.stand.getX());
        float z = (float) (this.getPrimaryGate().gatePos.getZ() + 0.5D - this.stand.getZ());
        float dir = this.gateOpenDirX * x + this.gateOpenDirZ * z;
        if (dir < 0.0F) {
            this.passed = true;
        }
    }

    protected FenceGate getPrimaryGate() {
        return this.below;
    }

    protected class AboveFenceGate extends FenceGate {
        @Override
        protected BlockPos offset(BlockPos pos) {
            return pos.above();
        }
    }

    protected class FenceGate {
        protected BlockPos gatePos = BlockPos.ZERO;
        protected boolean hasGate;

        protected void open() {
            if (this.isOpen()) {
                return;
            }

            this.openOrClose();
            AutoFenceGateGoal.this.stand.swing(InteractionHand.MAIN_HAND);
        }

        protected void close() {
            if (!this.isOpen()) {
                return;
            }

            this.openOrClose();
            AutoFenceGateGoal.this.stand.swing(InteractionHand.MAIN_HAND);
        }

        protected boolean isOpen() {
            if (!this.hasGate) {
                return false;
            } else {
                var blockState = AutoFenceGateGoal.this.stand.level().getBlockState(this.gatePos);
                if (!(blockState.getBlock() instanceof FenceGateBlock)) {
                    this.hasGate = false;
                    return false;
                } else {
                    return blockState.getValue(FenceGateBlock.OPEN);
                }
            }
        }

        protected void openOrClose() {
            if (this.hasGate && CommonConfig.standCanOpenGate) {
                var blockState = AutoFenceGateGoal.this.stand.level().getBlockState(this.gatePos);
                if (blockState.getBlock() instanceof FenceGateBlockInvoker gate) {
                    if (this.isOpen()) {
                        blockState = blockState.setValue(FenceGateBlock.OPEN, false);
                        AutoFenceGateGoal.this.stand.level().setBlock(this.gatePos, blockState, 10);
                    } else {
                        var direction = AutoFenceGateGoal.this.stand.getOwner().getDirection();
                        if (blockState.getValue(FenceGateBlock.FACING) == direction.getOpposite()) {
                            blockState = blockState.setValue(FenceGateBlock.FACING, direction);
                        }

                        blockState = blockState.setValue(FenceGateBlock.OPEN, true);
                        AutoFenceGateGoal.this.stand.level().setBlock(this.gatePos, blockState, 10);
                    }

                    boolean open = blockState.getValue(FenceGateBlock.OPEN);
                    AutoFenceGateGoal.this.stand.level().playSound(AutoFenceGateGoal.this.stand, this.gatePos, open ? gate.getOpenSound() : gate.getCloseSound(), SoundSource.BLOCKS, 1.0F, AutoFenceGateGoal.this.stand.getRandom().nextFloat() * 0.1F + 0.9F);
                    AutoFenceGateGoal.this.stand.level().gameEvent(AutoFenceGateGoal.this.stand, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, this.gatePos);
                }
            }
        }

        protected Direction getGateDirection(BlockPos pos) {
            var state = AutoFenceGateGoal.this.stand.level().getBlockState(pos);
            return state.getBlock() instanceof FenceGateBlock ? state.getValue(FenceGateBlock.FACING) : Direction.UP;
        }

        protected boolean isInFrontOfGate() {
            var owner = AutoFenceGateGoal.this.stand.getOwner();
            this.gatePos = this.offset(owner.blockPosition());
            var dir = this.getGateDirection(this.gatePos);
            this.hasGate = dir == owner.getDirection() || dir.getOpposite() == owner.getDirection();
            return this.hasGate;
        }

        protected BlockPos offset(BlockPos pos) {
            return pos;
        }
    }
}
