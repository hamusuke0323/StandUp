package com.hamusuke.standup.world.inventory;

import com.hamusuke.standup.invoker.PlayerInvoker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static com.hamusuke.standup.registry.RegisteredMenus.CARD_MENU;

public class StandCardMenu extends AbstractContainerMenu {
    private final Container cardContainer;

    public StandCardMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, new SimpleContainer(1));
    }

    public StandCardMenu(int id, Inventory inventory, Container container) {
        super(CARD_MENU.get(), id);
        this.cardContainer = container;
        checkContainerSize(container, 1);
        container.setItem(0, ((PlayerInvoker) inventory.player).getStandCard());
        container.startOpen(inventory.player);

        this.addSlot(new CardSlot(container, 0, 80, 20));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, i * 18 + 51));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.cardContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        var itemStack = ItemStack.EMPTY;
        var slot = this.slots.get(i);
        if (slot.hasItem()) {
            var item = slot.getItem();
            itemStack = item.copy();
            if (i < this.cardContainer.getContainerSize()) {
                if (!this.moveItemStackTo(item, this.cardContainer.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(item, 0, this.cardContainer.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (item.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.cardContainer.stopOpen(player);
        ((PlayerInvoker) player).setStandCard(this.cardContainer.getItem(0));
    }
}
