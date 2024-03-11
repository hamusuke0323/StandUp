package com.hamusuke.standup.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MthH {
    public static final float SQRT_OF_THREE = Mth.sqrt(3.0F);

    public static Vec3 front(Entity entity) {
        return entity.position().add(frontVector(entity.getYRot()));
    }

    public static Vec3 behind(Entity entity) {
        return entity.position().add(behindVector(entity.getYRot()));
    }

    public static Vec3 frontVector(Entity entity) {
        return frontVector(entity.getYRot());
    }

    public static Vec3 behindVector(Entity entity) {
        return behindVector(entity.getYRot());
    }

    public static Vec3 frontVector(float yaw) {
        float f1 = -yaw * Mth.DEG_TO_RAD;
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);

        return new Vec3(f3, 0.0D, f2);
    }

    public static Vec3 behindVector(float yaw) {
        return frontVector(yaw).reverse();
    }
}
