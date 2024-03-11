package com.hamusuke.standup.stand.ai.goal.target;

import com.hamusuke.standup.stand.Stand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class StandOwnerHurtTargetGoal extends TargetGoal {
    protected final Stand stand;
    protected LivingEntity ownerLastHurt;
    protected int timestamp;

    public StandOwnerHurtTargetGoal(Stand stand) {
        super(stand, false);

        this.stand = stand;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.stand.getOwner() != null) {
            this.ownerLastHurt = this.stand.getOwner().getLastHurtMob();
            int timestamp = this.stand.getOwner().getLastHurtMobTimestamp();
            return timestamp != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT);
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.stand.addTarget(this.ownerLastHurt);
        var owner = this.stand.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtMobTimestamp();
        }

        super.start();
    }
}
