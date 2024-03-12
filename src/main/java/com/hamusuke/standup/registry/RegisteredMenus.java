package com.hamusuke.standup.registry;

import com.hamusuke.standup.world.inventory.StandCardMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hamusuke.standup.StandUp.MOD_ID;

public class RegisteredMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<StandCardMenu>> CARD_MENU = MENU_TYPES.register("stand_card_menu", () -> IForgeMenuType.create(StandCardMenu::new));
}
