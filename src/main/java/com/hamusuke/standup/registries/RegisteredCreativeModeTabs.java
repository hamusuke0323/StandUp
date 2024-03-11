package com.hamusuke.standup.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;

import static com.hamusuke.standup.StandUp.MOD_ID;
import static com.hamusuke.standup.registries.RegisteredItems.EXAMPLE_ITEM;

public class RegisteredCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    static {
        CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
                .displayItems(RegisteredCreativeModeTabs::registerItemsToCreativeModeTabs).build());
    }

    private static void registerItemsToCreativeModeTabs(ItemDisplayParameters param, Output output) {
        output.accept(EXAMPLE_ITEM.get());
    }
}
