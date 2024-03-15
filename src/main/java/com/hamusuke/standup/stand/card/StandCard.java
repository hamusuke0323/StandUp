package com.hamusuke.standup.stand.card;

import com.hamusuke.standup.StandUp;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import static com.hamusuke.standup.StandUp.MOD_ID;

public abstract class StandCard {
    public static final StandCard EMPTY = new StandCard() {
        @Override
        public String getId() {
            return "";
        }
    };

    public static StandCard getCard(@Nullable ResourceLocation location) {
        if (location == null) {
            return EMPTY;
        }

        var card = StandUp.getReg().get().getValue(location);
        return card == null ? EMPTY : card;
    }

    public static StandCard getCard(String id) {
        return getCard(ResourceLocation.tryParse(id));
    }

    public ResourceLocation getCardId() {
        var location = StandUp.getReg().get().getKey(this);
        return location == null ? ResourceLocation.tryParse(this.getId()) : location;
    }

    public abstract String getId();

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public ResourceLocation getStandTexture() {
        return null;
    }

    public Component getTranslatableComponent() {
        return Component.translatable(MOD_ID + ".cards." + this.getId());
    }

    public Component getDisplayName() {
        return this.getTranslatableComponent();
    }

    public Stand createStand(Level level, Player owner, boolean slim) {
        return new Stand(level, owner, slim, this);
    }
}
