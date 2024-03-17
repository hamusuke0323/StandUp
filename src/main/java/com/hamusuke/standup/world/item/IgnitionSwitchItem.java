package com.hamusuke.standup.world.item;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class IgnitionSwitchItem extends Item {
    public IgnitionSwitchItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant().setNoRepair());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        var itemstack = p_41433_.getItemInHand(p_41434_);
        if (!p_41432_.isClientSide) {
            if (p_41433_ instanceof PlayerInvoker invoker && invoker.getStand() instanceof DeadlyQueen queen) {
                queen.igniteBomb();
            }

            p_41433_.getInventory().removeItem(itemstack);
            p_41433_.containerMenu.broadcastChanges();
        }

        return InteractionResultHolder.sidedSuccess(itemstack, p_41432_.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        return this.use(p_41427_.getLevel(), p_41427_.getPlayer(), p_41427_.getHand()).getResult();
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }
}
