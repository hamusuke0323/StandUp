package com.hamusuke.standup.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public final class Config {
    public static CommonConfig getCommonConfig() {
        return CommonConfig.INSTANCE;
    }

    @OnlyIn(Dist.CLIENT)
    public static ClientConfig getClientConfig() {
        return ClientConfig.INSTANCE;
    }

    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(Type.COMMON, CommonConfig.SPEC);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ModLoadingContext.get().registerConfig(Type.CLIENT, ClientConfig.SPEC));
    }

    @OnlyIn(Dist.CLIENT)
    public static void save() {
        CommonConfig.SPEC.save();
        ClientConfig.SPEC.save();
    }
}
