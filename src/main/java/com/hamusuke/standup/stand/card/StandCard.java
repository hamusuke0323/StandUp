package com.hamusuke.standup.stand.card;

import com.hamusuke.standup.StandUp;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

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

    @OnlyIn(Dist.CLIENT)
    public StandModelType getStandModelType() {
        return StandModelType.DEPENDS_ON_OWNER;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSlim(AbstractClientPlayer player) {
        return this.getStandModelType().isSlim(player);
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

    @OnlyIn(Dist.CLIENT)
    public enum StandModelType {
        DEPENDS_ON_OWNER(player -> player.getSkin().model() == Model.SLIM),
        SLIM(player -> true),
        WIDE(player -> false);

        private final Predicate<AbstractClientPlayer> slimTester;

        StandModelType(Predicate<AbstractClientPlayer> slimTester) {
            this.slimTester = slimTester;
        }

        public boolean isSlim(AbstractClientPlayer player) {
            return this.slimTester.test(player);
        }
    }
}
