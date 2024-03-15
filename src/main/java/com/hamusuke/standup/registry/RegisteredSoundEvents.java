package com.hamusuke.standup.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hamusuke.standup.StandUp.MOD_ID;

public class RegisteredSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final ResourceLocation PUNCH_SE = new ResourceLocation(MOD_ID, "punch");
    public static final ResourceLocation BOOM_SE = new ResourceLocation(MOD_ID, "stand.deadly_queen.boom");
    public static final ResourceLocation APPEAR_SE = new ResourceLocation(MOD_ID, "stand.deadly_queen.appear");
    public static final ResourceLocation CLICK_SE = new ResourceLocation(MOD_ID, "stand.deadly_queen.click");
    public static final RegistryObject<SoundEvent> PUNCH = SOUND_EVENTS.register("punch", () -> SoundEvent.createVariableRangeEvent(PUNCH_SE));
    public static final RegistryObject<SoundEvent> BOOM = SOUND_EVENTS.register("boom", () -> SoundEvent.createVariableRangeEvent(BOOM_SE));
    public static final RegistryObject<SoundEvent> APPEAR = SOUND_EVENTS.register("appear", () -> SoundEvent.createVariableRangeEvent(APPEAR_SE));
    public static final RegistryObject<SoundEvent> CLICK = SOUND_EVENTS.register("click", () -> SoundEvent.createVariableRangeEvent(CLICK_SE));
}
