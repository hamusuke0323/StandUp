package com.hamusuke.standup.stand.card.advanced;

import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.stand.stands.DeadlyQueen;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static com.hamusuke.standup.StandUp.MOD_ID;

public class DeadlyQueenCard extends StandCard {
    private static final ResourceLocation DEADLY_QUEEN = new ResourceLocation(MOD_ID, "textures/stand/deadly_queen.png");

    @Override
    public String getId() {
        return "killer_queen";
    }

    @Override
    public Stand createStand(Level level, Player owner, boolean slim) {
        return new DeadlyQueen(level, owner, slim, this);
    }

    @Override
    public @Nullable ResourceLocation getStandTexture() {
        return DEADLY_QUEEN;
    }

    @Override
    public StandModelType getStandModelType() {
        return StandModelType.WIDE;
    }
}
