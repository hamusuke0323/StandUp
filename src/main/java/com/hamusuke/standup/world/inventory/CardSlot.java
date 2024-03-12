package com.hamusuke.standup.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static com.hamusuke.standup.registry.RegisteredItems.STAND_CARD;

public class CardSlot extends Slot {
    public CardSlot(Container cardContainer, int slot, int x, int y) {
        super(cardContainer, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack p_40231_) {
        return p_40231_.is(STAND_CARD.get());
    }
}
