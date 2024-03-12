package com.hamusuke.standup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Objects;

import static com.hamusuke.standup.StandUp.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final Builder BUILDER = new Builder();
    static final ForgeConfigSpec SPEC = BUILDER.build();
    private static final BooleanValue STAND_CAN_OPEN_DOOR = BUILDER
            .comment("Whether stands can open doors.")
            .define("standCanOpenDoor", true);
    private static final BooleanValue STAND_CAN_OPEN_GATE = BUILDER
            .comment("Whether stands can open fence gates.")
            .define("standCanOpenGate", true);
    private static final BooleanValue SET_STAND_CARD_MSG = BUILDER
            .comment("Whether to send notify message when set stand card.")
            .define("setStandCardMsg", true);
    public static boolean standCanOpenDoor;
    public static boolean standCanOpenGate;
    public static boolean setStandCardMsg;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (Objects.equals(event.getConfig().getModId(), MOD_ID)) {
            standCanOpenDoor = STAND_CAN_OPEN_DOOR.get();
            standCanOpenGate = STAND_CAN_OPEN_GATE.get();
            setStandCardMsg = SET_STAND_CARD_MSG.get();
        }
    }

    public static void save() {
        STAND_CAN_OPEN_DOOR.set(standCanOpenDoor);
        STAND_CAN_OPEN_GATE.set(standCanOpenGate);
        SET_STAND_CARD_MSG.set(setStandCardMsg);
        SPEC.save();
    }
}
