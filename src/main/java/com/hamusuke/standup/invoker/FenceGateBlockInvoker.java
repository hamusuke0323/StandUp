package com.hamusuke.standup.invoker;

import net.minecraft.sounds.SoundEvent;

public interface FenceGateBlockInvoker {
    SoundEvent getOpenSound();

    SoundEvent getCloseSound();
}
