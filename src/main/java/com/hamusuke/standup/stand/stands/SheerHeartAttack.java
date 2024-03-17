package com.hamusuke.standup.stand.stands;

import static com.hamusuke.standup.registry.RegisteredEntities.SHEER_HEART_ATTACK;

public class SheerHeartAttack extends PartStand<DeadlyQueen> {
    protected DeadlyQueen parent;

    public SheerHeartAttack(DeadlyQueen parent) {
        super(SHEER_HEART_ATTACK.get(), parent);
        this.parent = parent;
    }
}
