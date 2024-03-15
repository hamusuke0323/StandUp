package com.hamusuke.standup.stand.card.advanced;

import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.stand.stands.StarPlatinum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static com.hamusuke.standup.StandUp.MOD_ID;

public class StarPlatinumCard extends StandCard {
    private static final ResourceLocation STAR_PLATINUM = new ResourceLocation(MOD_ID, "textures/stand/star_platinum.png");

    @Override
    public String getId() {
        return "star_platinum";
    }

    @Override
    public Stand createStand(Level level, Player owner, boolean slim) {
        return new StarPlatinum(level, owner, slim, this);
    }

    @Override
    public @Nullable ResourceLocation getStandTexture() {
        return STAR_PLATINUM;
    }

    @Override
    public StandModelType getStandModelType() {
        return StandModelType.WIDE;
    }
}
