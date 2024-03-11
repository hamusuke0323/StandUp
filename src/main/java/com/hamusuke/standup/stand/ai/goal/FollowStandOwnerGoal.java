package com.hamusuke.standup.stand.ai.goal;

import com.hamusuke.standup.stand.Stand;
import com.hamusuke.standup.util.MthH;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class FollowStandOwnerGoal extends Goal {
    private final Stand stand;
    private LivingEntity owner;
    private final double speedModifier;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;

    public FollowStandOwnerGoal(Stand stand, double p_25295_, float p_25296_, float p_25297_) {
        this.stand = stand;
        this.speedModifier = p_25295_;
        this.navigation = stand.getNavigation();
        this.startDistance = p_25296_;
        this.stopDistance = p_25297_;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        if (!(stand.getNavigation() instanceof GroundPathNavigation) && !(stand.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowStandOwnerGoal");
        }
    }

    @Override
    public boolean canUse() {
        var standOwner = this.stand.getOwner();
        if (standOwner == null || standOwner.isSpectator() || this.unableToMove() || (this.stand.isAggressive() && !this.stand.isTooFarAway())) {
            return false;
        } else if (!standOwner.walkAnimation.isMoving() && this.stand.distanceToSqr(standOwner) < (double) (this.startDistance * this.startDistance)) {
            return false;
        } else {
            this.owner = standOwner;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
        } else if (this.unableToMove()) {
            return false;
        } else {
            return !this.stand.isAggressive() && !(this.stand.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
        }
    }

    private boolean unableToMove() {
        return !this.stand.isFollowingOwner() || this.stand.isPassenger();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        this.stand.getLookControl().setLookAt(this.owner, 10.0F, (float) this.stand.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(2);
            if (this.stand.isTooFarAway()) {
                this.teleportToOwner();
            } else {
                var pos = MthH.behind(this.owner);
                this.navigation.moveTo(pos.x, pos.y, pos.z, this.speedModifier);
            }
        }
    }

    private void teleportToOwner() {
        this.stand.moveTo(this.owner.position().add(MthH.behindVector(this.owner).scale(this.stand.getRandom().nextDouble())));
        this.navigation.stop();
    }
}
