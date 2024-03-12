package com.hamusuke.standup.stand.ability;

import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static com.hamusuke.standup.StandUp.MOD_ID;

public abstract class StandCard {
    public static final StandCard EMPTY = new StandCard() {
        @Override
        public String getId() {
            return "";
        }
    };

    public abstract String getId();

    public Component getTranslatableComponent() {
        return Component.translatable(MOD_ID + ".cards." + this.getId());
    }

    public Component getDisplayName() {
        return this.getTranslatableComponent();
    }

    public Stand createStand(Level level, Player owner, boolean slim) {
        return new Stand(slim, level, owner);
    }
}
