package com.hamusuke.standup.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public final class CommonConfig {
    static final CommonConfig INSTANCE;
    static final ForgeConfigSpec SPEC;

    static {
        var pair = new Builder().configure(CommonConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final BooleanValue standCanOpenDoor;
    public final BooleanValue standCanOpenGate;

    private CommonConfig(Builder builder) {
        this.standCanOpenDoor = builder
                .comment("Whether stands can open doors.")
                .define("standCanOpenDoor", true);
        this.standCanOpenGate = builder
                .comment("Whether stands can open fence gates.")
                .define("standCanOpenGate", true);
    }
}
