package com.hamusuke.standup.stand.ai.goal;

import com.hamusuke.standup.stand.Stand;
import com.hamusuke.standup.util.MthH;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class StandRushAttackGoal extends Goal {
    protected final Stand stand;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    protected int ticksUntilNextAttack;
    protected int attackInterval;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;
    private final boolean canPenalize = false;
    private InteractionHand lastPunchedHand = InteractionHand.OFF_HAND;

    public StandRushAttackGoal(Stand stand, double speed, int attackInterval, boolean followingTargetEvenIfNotSeen) {
        this.stand = stand;
        this.speedModifier = speed;
        this.attackInterval = attackInterval;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        var livingentity = this.stand.getNearestTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.stand.isAlive() || !this.stand.getOwner().closerThan(livingentity, this.stand.maxMovableDistanceFromPlayer())) {
            return false;
        } else if (this.canPenalize) {
            if (--this.ticksUntilNextPathRecalculation <= 0) {
                this.path = this.stand.getNavigation().createPath(livingentity, 0);
                this.ticksUntilNextPathRecalculation = 4 + this.stand.getRandom().nextInt(7);
                return this.path != null;
            } else {
                return true;
            }
        } else {
            this.path = this.stand.getNavigation().createPath(livingentity, 0);
            return this.path != null || this.stand.isWithinMeleeAttackRange(livingentity);
        }
    }

    @Override
    public boolean canContinueToUse() {
        var livingentity = this.stand.getNearestTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.stand.getNavigation().isDone();
        } else if (!this.stand.isWithinRestriction(livingentity.blockPosition())) {
            return false;
        } else if (!this.stand.isAlive() || !this.stand.getOwner().closerThan(livingentity, this.stand.maxMovableDistanceFromPlayer())) {
            return false;
        } else {
            return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative();
        }
    }

    @Override
    public void start() {
        this.stand.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        var livingentity = this.stand.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            this.stand.setTarget(null);
        }

        this.stand.setAggressive(false);
        this.stand.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        var livingentity = this.stand.getNearestTarget();
        if (livingentity != null) {
            this.stand.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.stand.getSensing().hasLineOfSight(livingentity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || livingentity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.stand.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = livingentity.getX();
                this.pathedTargetY = livingentity.getY();
                this.pathedTargetZ = livingentity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.stand.getRandom().nextInt(7);
                double d0 = this.stand.distanceToSqr(livingentity);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += this.failedPathFindingPenalty;
                    if (this.stand.getNavigation().getPath() != null) {
                        Node finalPathPoint = this.stand.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                            this.failedPathFindingPenalty = 0;
                        } else {
                            this.failedPathFindingPenalty += 10;
                        }
                    } else {
                        this.failedPathFindingPenalty += 10;
                    }
                }

                if (d0 > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                this.stand.moveTo(MthH.front(livingentity));

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(livingentity);
        }

    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            this.lastPunchedHand = this.lastPunchedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            this.stand.swing(this.lastPunchedHand);
            this.stand.doHurtTarget(target);
            this.stand.moveTo(MthH.front(target).subtract(0.0D, this.stand.getEyeHeight() - target.getEyeHeight(), 0.0D));
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(this.attackInterval);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity p_301160_) {
        return this.isTimeToAttack() && this.stand.isWithinMeleeAttackRange(p_301160_);
    }
}
