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
    public static final RegistryObject<SoundEvent> PUNCH = SOUND_EVENTS.register("punch", () -> SoundEvent.createVariableRangeEvent(PUNCH_SE));
}
