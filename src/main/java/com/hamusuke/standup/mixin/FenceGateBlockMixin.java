package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.FenceGateBlockInvoker;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.FenceGateBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FenceGateBlock.class)
public abstract class FenceGateBlockMixin implements FenceGateBlockInvoker {
    @Shadow
    @Final
    private SoundEvent openSound;

    @Shadow
    @Final
    private SoundEvent closeSound;

    @Override
    public SoundEvent getOpenSound() {
        return this.openSound;
    }

    @Override
    public SoundEvent getCloseSound() {
        return this.closeSound;
    }
}
