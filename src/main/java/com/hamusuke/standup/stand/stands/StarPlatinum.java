package com.hamusuke.standup.stand.stands;

import com.hamusuke.standup.stand.ai.goal.StandRushAttackGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandMultipleAttackableTargetsGoal;
import com.hamusuke.standup.stand.ai.goal.target.StandOwnerHurtTargetGoal;
import com.hamusuke.standup.stand.card.StandCard;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class StarPlatinum extends Stand {
    public StarPlatinum(Level level, Player owner, boolean slim, StandCard card) {
        super(level, owner, slim, card);
    }

    @Override
    public double maxMovableDistanceFromPlayer() {
        return 5.0D;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new StandRushAttackGoal(this, 2, true));

        this.targetSelector.addGoal(1, new StandOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new StandMultipleAttackableTargetsGoal<>(this, LivingEntity.class, false, livingEntity -> {
            return !this.getOwner().isCreative() && livingEntity instanceof Enemy;
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
