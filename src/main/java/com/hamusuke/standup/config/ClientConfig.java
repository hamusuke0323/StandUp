package com.hamusuke.standup.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

@OnlyIn(Dist.CLIENT)
public final class ClientConfig {
    static final ClientConfig INSTANCE;
    static final ForgeConfigSpec SPEC;

    static {
        var pair = new Builder().configure(ClientConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final BooleanValue setStandCardMsg;

    private ClientConfig(Builder builder) {
        this.setStandCardMsg = builder
                .comment("Whether to send notify message when set stand card.")
                .define("setStandCardMsg", true);
    }
}
