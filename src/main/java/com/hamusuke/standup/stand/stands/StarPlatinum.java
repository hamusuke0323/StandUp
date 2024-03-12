package com.hamusuke.standup.stand.stands;

import com.hamusuke.standup.stand.ai.goal.AutoDoorGoal;
import com.hamusuke.standup.stand.ai.goal.AutoFenceGateGoal;
import com.hamusuke.standup.stand.ai.goal.FollowStandOwnerGoal;
import com.hamusuke.standup.stand.ai.goal.StandRushAttackGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandMultipleAttackableTargetsGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandOwnerHurtTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class StarPlatinum extends Stand {
    public StarPlatinum(boolean slim, Level level, @Nullable Player owner) {
        super(slim, level, owner);
    }

    @Override
    public double maxMovableDistanceFromPlayer() {
        return 5.0D;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new StandRushAttackGoal(this, 10.0D, 2, true));
        this.goalSelector.addGoal(5, new AutoDoorGoal(this));
        this.goalSelector.addGoal(5, new AutoFenceGateGoal(this));
        this.goalSelector.addGoal(10, new FollowStandOwnerGoal(this, 5.0D, 2.5F, 0.1F));

        this.targetSelector.addGoal(1, new StandOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new StandMultipleAttackableTargetsGoal<>(this, LivingEntity.class, false, livingEntity -> {
            return this.getOwner() != null && !this.getOwner().isCreative() && livingEntity instanceof Enemy;
        }));
    }

    @Override
    public double getFollowRange() {
        return 5.0D;
    }

    @Override
    public double getAttackDamage() {
        return 5.0D;
    }
}
