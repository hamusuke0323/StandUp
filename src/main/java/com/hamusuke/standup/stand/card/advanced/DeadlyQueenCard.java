package com.hamusuke.standup.stand.card.advanced;

import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.stand.stands.DeadlyQueen;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DeadlyQueenCard extends StandCard {
    @Override
    public String getId() {
        return "killer_queen";
    }

    @Override
    public Stand createStand(Level level, Player owner, boolean slim) {
        return new DeadlyQueen(level, owner, slim, this);
    }
}
