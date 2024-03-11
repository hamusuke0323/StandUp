package com.hamusuke.standup;

import com.google.common.collect.Sets;
import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.registries.RegisteredCreativeModeTabs;
import com.hamusuke.standup.registries.RegisteredEntities;
import com.hamusuke.standup.registries.RegisteredItems;
import com.hamusuke.standup.registries.RegisteredSoundEvents;
import com.hamusuke.standup.stand.Stand;
import com.hamusuke.standup.stand.Stand.StandOperationMode;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Set;
import java.util.function.Consumer;

@Mod(StandUp.MOD_ID)
public class StandUp {
    public static final String MOD_ID = "standup";
    private static final Set<Consumer<IEventBus>> REGISTRIES = Sets.newHashSet(
            RegisteredCreativeModeTabs.CREATIVE_MODE_TABS::register,
            RegisteredEntities.ENTITY_TYPES::register,
            RegisteredItems.ITEMS::register,
            RegisteredSoundEvents.SOUND_EVENTS::register
    );

    public StandUp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRIES.forEach(act -> act.accept(modEventBus));

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkManager.registerPackets();
    }

    @SubscribeEvent
    public void onKnockBack(final LivingKnockBackEvent event) {
        if (event.getEntity().getLastHurtByMob() instanceof Stand stand && !stand.isControlledByStandOwner()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingAttack(final LivingAttackEvent event) {
        if (event.getEntity() == event.getSource().getEntity() && event.getEntity() instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            event.setCanceled(true);
        } else if (event.getEntity() instanceof PlayerInvoker invoker && invoker.isStandAlive() && !invoker.isControllingStand()) {
            if (event.getSource().getEntity() instanceof LivingEntity living && invoker.getStand().canAttack(living)) {
                invoker.getStand().addTarget(living);
            }
        }
    }

    @SubscribeEvent
    public void onMount(final EntityMountEvent event) {
        if (event.isMounting() && event.getEntityMounting() instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            invoker.getStand().toggleMode(StandOperationMode.AI);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(final PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof PlayerInvoker invoker && invoker.isControllingStand() && invoker.getStand().isHoldingOwner()) {
            invoker.getStand().stopHoldingOwner();
        }
    }
}
