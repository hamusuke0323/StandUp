package com.hamusuke.standup.stand;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static com.hamusuke.standup.registries.RegisteredEntities.SLIM_STAND_TYPE;

public class SlimStand extends Stand {
    public SlimStand(Level p_21369_, @Nullable Player owner) {
        super(SLIM_STAND_TYPE.get(), p_21369_, owner);
    }
}
