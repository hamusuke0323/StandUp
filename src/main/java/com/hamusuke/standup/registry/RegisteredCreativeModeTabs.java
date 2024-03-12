package com.hamusuke.standup.registry;

import com.hamusuke.standup.world.item.StandCardItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hamusuke.standup.StandUp.MOD_ID;
import static com.hamusuke.standup.registry.RegisteredItems.STAND_CARD;
import static com.hamusuke.standup.registry.RegisteredStandCards.STAND_CARDS;

public class RegisteredCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    static {
        CREATIVE_MODE_TABS.register("stand_up_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable(MOD_ID + ".tab.title"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> STAND_CARD.get().getDefaultInstance())
                .displayItems(RegisteredCreativeModeTabs::registerItemsToCreativeModeTabs).build());
    }

    private static void registerItemsToCreativeModeTabs(ItemDisplayParameters param, Output output) {
        STAND_CARDS.getEntries().stream().map(RegistryObject::get).forEach(card -> output.accept(StandCardItem.createForStandCard(card)));
    }
}
