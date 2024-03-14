package com.hamusuke.standup.stand.stands;

import com.hamusuke.standup.stand.ability.StandCard;
import com.hamusuke.standup.stand.ai.goal.StandRushAttackGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandMultipleAttackableTargetsGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandOwnerHurtTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class KillerQueen extends Stand {
    public KillerQueen(Level level, Player owner, boolean slim, StandCard standCardId) {
        super(level, owner, slim, standCardId);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new StandRushAttackGoal(this, 3, true));

        this.targetSelector.addGoal(1, new StandOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new StandMultipleAttackableTargetsGoal<>(this, LivingEntity.class, false, livingEntity -> {
            return !this.getOwner().isCreative() && livingEntity instanceof Enemy;
        }));
    }

    @Override
    public double maxMovableDistanceFromPlayer() {
        return 8.0D;
    }

    @Override
    public double getFollowRange() {
        return 8.0D;
    }
}
