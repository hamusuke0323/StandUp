package com.hamusuke.standup.world.item;

import com.hamusuke.standup.stand.card.StandCard;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.hamusuke.standup.registry.RegisteredItems.STAND_CARD;

public class StandCardItem extends Item {
    public StandCardItem() {
        super(new Properties().fireResistant().rarity(Rarity.EPIC).stacksTo(1).setNoRepair());
    }

    public static void setStandCard(ItemStack stack, StandCard standCard) {
        var key = standCard.getCardId();
        if (key != null && !key.getPath().isBlank()) {
            stack.getOrCreateTag().putString("id", key.toString());
        }
    }

    public static ItemStack createForStandCard(StandCard standCard) {
        var itemStack = new ItemStack(STAND_CARD.get());
        setStandCard(itemStack, standCard);
        return itemStack;
    }

    public static boolean hasStandCard(ItemStack stack) {
        return getStandCardFrom(stack) != StandCard.EMPTY;
    }

    public static StandCard getStandCardFrom(ItemStack stack) {
        var s = stack.getOrCreateTag().getString("id");
        return StandCard.getCard(s);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        p_41423_.add(getStandCardFrom(p_41421_).getDisplayName());
    }

    @Override
    public boolean canBeHurtBy(DamageSource p_41387_) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canAttackBlock(BlockState p_41441_, Level p_41442_, BlockPos p_41443_, Player p_41444_) {
        return false;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return false;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return false;
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }
}
