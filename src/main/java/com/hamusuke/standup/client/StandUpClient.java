package com.hamusuke.standup.client;

import com.hamusuke.standup.client.gui.screen.CardMenuScreen;
import com.hamusuke.standup.client.gui.screen.ConfigScreen;
import com.hamusuke.standup.client.renderer.entity.SHARenderer;
import com.hamusuke.standup.client.renderer.entity.StandRenderer;
import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.c2s.*;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.What;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.When;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.world.item.StandCardItem;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.Clone;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static com.hamusuke.standup.StandUp.MOD_ID;
import static com.hamusuke.standup.registry.RegisteredEntities.*;
import static com.hamusuke.standup.registry.RegisteredMenus.CARD_MENU;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StandUpClient {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final KeyMapping STAND_UP_DOWN = new KeyMapping(MOD_ID + ".key.stand.updown", GLFW.GLFW_KEY_Z, MOD_ID + ".key.category");
    private static final KeyMapping HOLD_OR_RELEASE_OWNER = new KeyMapping(MOD_ID + ".key.stand.holdorrelease", GLFW.GLFW_KEY_C, MOD_ID + ".key.category");
    private static final KeyMapping TOGGLE_STAND_OPERATION_MODE = new KeyMapping(MOD_ID + ".key.stand.toggle", GLFW.GLFW_KEY_X, MOD_ID + ".key.category");
    private static final KeyMapping USE_STAND_ABILITY = new KeyMapping(MOD_ID + ".key.stand.ability", GLFW.GLFW_KEY_V, MOD_ID + ".key.category");
    private static final KeyMapping OPEN_STAND_CARD_MENU = new KeyMapping(MOD_ID + ".key.stand.open.menu", GLFW.GLFW_KEY_B, MOD_ID + ".key.category");
    private static StandUpClient INSTANCE;
    public static When whenRef = When.TOUCH;
    public static What whatRef = What.TOUCHING_ENTITY;

    private StandUpClient() {
        INSTANCE = this;
    }

    public static StandUpClient getInstance() {
        if (INSTANCE == null) {
            new StandUpClient();
        }

        return INSTANCE;
    }

    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(CARD_MENU.get(), CardMenuScreen::new);
        MinecraftForge.EVENT_BUS.register(StandUpClient.getInstance());
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class, () -> new ConfigScreenFactory(ConfigScreen::new));
    }

    @SubscribeEvent
    static void onRegisterKey(final RegisterKeyMappingsEvent event) {
        event.register(STAND_UP_DOWN);
        event.register(HOLD_OR_RELEASE_OWNER);
        event.register(TOGGLE_STAND_OPERATION_MODE);
        event.register(USE_STAND_ABILITY);
        event.register(OPEN_STAND_CARD_MENU);
    }

    @SubscribeEvent
    public void onRenderLevel(final RenderLevelStageEvent event) {
        if (event.getStage() == Stage.AFTER_ENTITIES && event.getCamera().getEntity() instanceof Stand stand && stand.getOwner() instanceof LocalPlayer player) {
            stand.setYRot(player.getYRot());
            stand.setXRot(player.getXRot());
            stand.setYBodyRot(player.yBodyRot);
            stand.yBodyRotO = player.yBodyRotO;
            stand.setYHeadRot(player.getYHeadRot());
        }
    }

    private static void requestToStandUp() {
        NetworkManager.sendToServer(new StandUpReq(StandCardItem.getStandCardFrom(PlayerInvoker.invoker(mc.player).getStandCard()).isSlim(mc.player)));
    }

    @SubscribeEvent
    static void onEntityRenderer(final RegisterRenderers event) {
        event.registerEntityRenderer(STAND_TYPE.get(), context -> new StandRenderer(context, false));
        event.registerEntityRenderer(SLIM_STAND_TYPE.get(), context -> new StandRenderer(context, true));
        event.registerEntityRenderer(SHEER_HEART_ATTACK.get(), SHARenderer::new);
    }

    @SubscribeEvent
    public void onTick(final ClientTickEvent event) {
        if (event.phase == Phase.END) {
            while (STAND_UP_DOWN.consumeClick()) {
                var invoker = PlayerInvoker.invoker(mc.player);
                if (invoker.isStandAlive()) {
                    NetworkManager.sendToServer(new StandDownReq());
                } else {
                    requestToStandUp();
                }
            }

            while (HOLD_OR_RELEASE_OWNER.consumeClick() && PlayerInvoker.invoker(mc.player).isStandAlive()) {
                NetworkManager.sendToServer(new HoldOrReleaseOwnerReq());
            }

            while (TOGGLE_STAND_OPERATION_MODE.consumeClick() && PlayerInvoker.invoker(mc.player).isStandAlive()) {
                NetworkManager.sendToServer(new StandOpModeToggleReq());
            }

            while (USE_STAND_ABILITY.consumeClick()) {
                var invoker = PlayerInvoker.invoker(mc.player);
                if (!invoker.isStandAlive()) {
                    requestToStandUp();
                }

                NetworkManager.sendToServer(new UseStandAbilityNotify(mc.hitResult));
            }

            while (OPEN_STAND_CARD_MENU.consumeClick()) {
                NetworkManager.sendToServer(new StandCardMenuOpenReq());
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayer(final RenderPlayerEvent.Pre event) {
        if (mc.screen == null && mc.options.getCameraType() == CameraType.FIRST_PERSON && event.getEntity() instanceof LocalPlayer localPlayer && localPlayer instanceof PlayerInvoker invoker && invoker.isControllingStand() && invoker.getStand().isHoldingOwner()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClone(Clone event) {
        var n = PlayerInvoker.invoker(event.getNewPlayer());
        var o = PlayerInvoker.invoker(event.getOldPlayer());
        n.setStandCard(o.getStandCard());
    }
}
