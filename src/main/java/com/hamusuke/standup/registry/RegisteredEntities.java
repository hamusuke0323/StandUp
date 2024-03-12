package com.hamusuke.standup.registry;

import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hamusuke.standup.StandUp.MOD_ID;

@EventBusSubscriber(bus = Bus.MOD, modid = MOD_ID)
public class RegisteredEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<Stand>> STAND_TYPE = ENTITY_TYPES.register("stand", () -> EntityType.Builder.<Stand>of((entityType, level) -> new Stand(level, null), MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2).build("stand"));
    public static final RegistryObject<EntityType<Stand>> SLIM_STAND_TYPE = ENTITY_TYPES.register("slim_stand", () -> EntityType.Builder.<Stand>of((entityType, level) -> new Stand(true, level, null), MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2).build("slim_stand"));

    @SubscribeEvent
    static void attributeCreation(final EntityAttributeCreationEvent event) {
        event.put(STAND_TYPE.get(), Stand.createAttributes());
        event.put(SLIM_STAND_TYPE.get(), Stand.createAttributes());
    }
}
