package com.hamusuke.standup.stand.ability;

import net.minecraft.resources.ResourceLocation;

public interface StandAbility {
    ResourceLocation getCardTexture();

    void onStandUp();

    void onStandDown();

    default boolean isRushAttackable() {
        return true;
    }

    default boolean canSprint() {
        return true;
    }
}
