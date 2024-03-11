package com.hamusuke.standup.mixin;

import com.hamusuke.standup.invoker.LivingEntityInvoker;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityInvoker {
    @Shadow
    protected int lastHurtByPlayerTime;

    @Override
    public void setLastHurtByPlayerTime(int time) {
        this.lastHurtByPlayerTime = time;
    }
}
