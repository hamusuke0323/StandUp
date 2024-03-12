package com.hamusuke.standup.stand.ai.goal.target;

import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class StandMultipleAttackableTargetsGoal<T extends LivingEntity, M extends Stand> extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 0;
    protected final Class<T> targetType;
    protected final int randomInterval;
    protected List<T> targets = Lists.newArrayList();
    protected TargetingConditions targetConditions;
    protected final M mob;

    public StandMultipleAttackableTargetsGoal(M p_26060_, Class<T> p_26061_, boolean p_26062_) {
        this(p_26060_, p_26061_, DEFAULT_RANDOM_INTERVAL, p_26062_, false, null);
    }

    public StandMultipleAttackableTargetsGoal(M p_199891_, Class<T> p_199892_, boolean p_199893_, Predicate<LivingEntity> p_199894_) {
        this(p_199891_, p_199892_, DEFAULT_RANDOM_INTERVAL, p_199893_, false, p_199894_);
    }

    public StandMultipleAttackableTargetsGoal(M p_26064_, Class<T> p_26065_, boolean p_26066_, boolean p_26067_) {
        this(p_26064_, p_26065_, DEFAULT_RANDOM_INTERVAL, p_26066_, p_26067_, null);
    }

    public StandMultipleAttackableTargetsGoal(M p_26053_, Class<T> p_26054_, int p_26055_, boolean p_26056_, boolean p_26057_, @Nullable Predicate<LivingEntity> p_26058_) {
        super(p_26053_, p_26056_, p_26057_);
        this.mob = p_26053_;
        this.targetType = p_26054_;
        this.randomInterval = reducedTickDelay(p_26055_);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(p_26058_);
    }

    @Override
    public boolean canUse() {
        this.findTarget();
        return !this.targets.isEmpty();
    }

    protected AABB getTargetSearchArea(double p_26069_) {
        return this.mob.getBoundingBox().inflate(p_26069_, 4.0, p_26069_);
    }

    protected void findTarget() {
        this.targets = this.mob.level().getNearbyEntities(this.targetType, this.targetConditions, this.mob, this.getTargetSearchArea(this.getFollowDistance()));
    }

    @Override
    protected double getFollowDistance() {
        return this.mob.getFollowRange();
    }

    @Override
    public void start() {
        this.targets.forEach(this.mob::addTarget);
        this.mob.setTarget(this.mob.getNearestTarget());
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getTargets().isEmpty();
    }
}
