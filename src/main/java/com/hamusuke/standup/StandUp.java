package com.hamusuke.standup;

import com.google.common.collect.Sets;
import com.hamusuke.standup.config.Config;
import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.s2c.StandCardSetNotify;
import com.hamusuke.standup.registry.*;
import com.hamusuke.standup.stand.card.StandCard;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.stand.stands.Stand.StandOperationMode;
import com.hamusuke.standup.world.item.StandCardItem;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(StandUp.MOD_ID)
public class StandUp {
    public static final String MOD_ID = "standup";
    public static final ResourceLocation STAND_CARD_REGISTRY_KEY = new ResourceLocation(MOD_ID, "stand_card");
    private static final Set<Consumer<IEventBus>> REGISTRIES = Sets.newHashSet(
            RegisteredCreativeModeTabs.CREATIVE_MODE_TABS::register,
            RegisteredEntities.ENTITY_TYPES::register,
            RegisteredItems.ITEMS::register,
            RegisteredMenus.MENU_TYPES::register,
            RegisteredSoundEvents.SOUND_EVENTS::register,
            RegisteredStandCards.STAND_CARDS::register
    );
    private static Supplier<IForgeRegistry<StandCard>> reg = () -> null;

    public StandUp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StandCardLoader.getInstance().getStandCards().forEach(card -> RegisteredStandCards.STAND_CARDS.register(card.getId(), () -> card));

        REGISTRIES.forEach(act -> act.accept(modEventBus));

        modEventBus.addListener(this::newRegistry);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        Config.registerConfigs();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkManager.registerPackets();
    }

    public static Supplier<IForgeRegistry<StandCard>> getReg() {
        return reg;
    }

    private void newRegistry(final NewRegistryEvent event) {
        reg = event.create(RegistryBuilder.of(STAND_CARD_REGISTRY_KEY));
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

    @SubscribeEvent
    public void onLivingFall(final LivingFallEvent event) {
        if (event.getEntity() instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToClient(new StandCardSetNotify(serverPlayer.getId(), ((PlayerInvoker) serverPlayer).getStandCard(), false), serverPlayer);
        }
    }

    @SubscribeEvent
    public void onChangedDim(final PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof PlayerInvoker invoker) {
            invoker.standDown();
        }
    }

    @SubscribeEvent
    public void onDeath(final LivingDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon dragon && dragon.getDragonFight() != null && !dragon.level().isClientSide) {
            var card = StandCardItem.createForStandCard(Util.getRandomSafe(StandUp.getReg().get().getValues().stream().toList(), dragon.getRandom()).orElse(StandCard.EMPTY));
            if (card.isEmpty() || !StandCardItem.hasStandCard(card)) {
                return;
            }

            var players = List.copyOf(dragon.level().players());
            if (players.isEmpty()) {
                return;
            }

            Util.getRandomSafe(players, dragon.getRandom()).ifPresent(player -> {
                if (player.getInventory().add(card)) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.containerMenu.broadcastChanges();
                } else {
                    var e = player.drop(card, false);
                    if (e != null) {
                        e.setNoPickUpDelay();
                        e.setTarget(player.getUUID());
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onToss(final ItemTossEvent event) {
        var player = event.getPlayer();
        if (player.level().isClientSide) {
            return;
        }

        if (player instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
            event.getEntity().setPos(invoker.getStand().getX(), invoker.getStand().getEyeY() - 0.30000001192092896D, invoker.getStand().getZ());
        }
    }

    @SubscribeEvent
    public void onClone(final Clone event) {
        var cloned = PlayerInvoker.invoker(event.getEntity());
        var origin = PlayerInvoker.invoker(event.getOriginal());

        cloned.setStandCard(origin.getStandCard());
    }
}
