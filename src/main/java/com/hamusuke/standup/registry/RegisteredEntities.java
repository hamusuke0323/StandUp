package com.hamusuke.standup.registry;

import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.stand.stands.DeadlyQueen;
import com.hamusuke.standup.stand.stands.PartStand;
import com.hamusuke.standup.stand.stands.SheerHeartAttack;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.packets.SpawnEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static com.hamusuke.standup.StandUp.MOD_ID;

@EventBusSubscriber(bus = Bus.MOD, modid = MOD_ID)
public class RegisteredEntities {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<Stand>> STAND_TYPE = ENTITY_TYPES.register("stand", () -> EntityType.Builder.<Stand>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2).setCustomClientFactory(RegisteredEntities::createClientSidedStand).build("stand"));
    public static final RegistryObject<EntityType<Stand>> SLIM_STAND_TYPE = ENTITY_TYPES.register("slim_stand", () -> EntityType.Builder.<Stand>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2).setCustomClientFactory(RegisteredEntities::createClientSidedSlimStand).build("slim_stand"));
    @SubscribeEvent
    static void attributeCreation(final EntityAttributeCreationEvent event) {
        event.put(STAND_TYPE.get(), Stand.createAttributes());
        event.put(SLIM_STAND_TYPE.get(), Stand.createAttributes());
        event.put(SHEER_HEART_ATTACK.get(), Stand.createAttributes());
    }    public static final RegistryObject<EntityType<SheerHeartAttack>> SHEER_HEART_ATTACK = ENTITY_TYPES.register("sheer_heart_attack", () -> EntityType.Builder.<SheerHeartAttack>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.8F, 0.6F).clientTrackingRange(32).updateInterval(2).setCustomClientFactory(RegisteredEntities::createClientSidedSHA).build("sheer_heart_attack"));

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static <Parent extends Stand, T extends PartStand<Parent>> T createClientSidedPartStand(Class<Parent> parentClass, Function<Parent, T> partCreator, SpawnEntity spawn, Level level) {
        var parent = level.getEntity(spawn.getAdditionalData().readVarInt());
        if (!parentClass.isInstance(parent)) {
            LOGGER.warn("Client-sided parent stand seems to have gone somewhere!?");
            return null;
        }

        return partCreator.apply(parentClass.cast(parent));
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static Stand createClientSidedStand(SpawnEntity spawn, Level level) {
        return createClientSidedStand(spawn, level, false);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static Stand createClientSidedSlimStand(SpawnEntity spawn, Level level) {
        return createClientSidedStand(spawn, level, true);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static Stand createClientSidedStand(SpawnEntity spawn, Level level, boolean slim) {
        var uuid = spawn.getAdditionalData().readUUID();
        var location = spawn.getAdditionalData().readResourceLocation();
        var owner = level.getPlayerByUUID(uuid);
        if (owner == null) {
            LOGGER.warn("Client-sided stand owner is null! This should never happen!");
            return null;
        }

        return StandCard.getCard(location).createStand(level, owner, slim);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    private static SheerHeartAttack createClientSidedSHA(SpawnEntity spawn, Level level) {
        return createClientSidedPartStand(DeadlyQueen.class, SheerHeartAttack::new, spawn, level);
    }


}
