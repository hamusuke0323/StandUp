package com.hamusuke.standup.mixin;

import com.hamusuke.standup.stand.Stand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ContainerOpenersCounter.class)
public abstract class ContainerOpenersCounterMixin {
    @Redirect(method = "getOpenCount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private List<LivingEntity> countOpeners(Level instance, EntityTypeTest<Entity, Player> p_151528_, AABB p_151529_, Predicate<? super Player> p_151530_) {
        return instance.getEntitiesOfClass(LivingEntity.class, p_151529_, livingEntity -> {
            if (livingEntity instanceof Player player) {
                return p_151530_.test(player);
            } else if (livingEntity instanceof Stand stand && stand.isAlive()) {
                return p_151530_.test(stand.getOwner());
            }

            return false;
        });
    }
}
