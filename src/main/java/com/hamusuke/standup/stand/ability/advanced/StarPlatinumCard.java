package com.hamusuke.standup.stand.ability.advanced;

import com.hamusuke.standup.stand.ability.StandCard;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.stand.stands.StarPlatinum;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class StarPlatinumCard extends StandCard {
    @Override
    public String getId() {
        return "star_platinum";
    }

    @Override
    public Stand createStand(Level level, Player owner, boolean slim) {
        return new StarPlatinum(level, owner, slim, this);
    }
}
