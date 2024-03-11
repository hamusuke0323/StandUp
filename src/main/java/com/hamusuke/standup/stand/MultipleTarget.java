package com.hamusuke.standup.stand;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface MultipleTarget {
    void addTarget(LivingEntity entity);

    @Nullable
    LivingEntity getNearestTarget();

    Set<LivingEntity> getTargets();

    boolean isTargetEmpty();
}
