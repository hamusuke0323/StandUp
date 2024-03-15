package com.hamusuke.standup.stand.stands;

import net.minecraft.world.entity.EntityType;

public class SheerHeartAttack extends Stand {
    public SheerHeartAttack(EntityType<? extends SheerHeartAttack> type, DeadlyQueen stand) {
        super(type, stand.level(), stand.owner, stand.standCard);
    }
}
