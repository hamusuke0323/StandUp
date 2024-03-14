package com.hamusuke.standup.stand.ability.advanced;

import com.hamusuke.standup.stand.ability.StandCard;
import com.hamusuke.standup.stand.stands.KillerQueen;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class KillerQueenCard extends StandCard {
    @Override
    public String getId() {
        return "killer_queen";
    }

    @Override
    public Stand createStand(Level level, Player owner, boolean slim) {
        return new KillerQueen(level, owner, slim, this);
    }
}
