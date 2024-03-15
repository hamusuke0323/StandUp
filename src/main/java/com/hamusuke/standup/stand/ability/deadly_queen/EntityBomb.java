package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityBomb extends Bomb {
    protected final Entity target;

    protected EntityBomb(DeadlyQueen stand, When explodeWhen, What whatExplodes, Entity target) {
        super(stand, explodeWhen, whatExplodes);

        this.target = target;
    }

    public static EntityBomb pushSwitchSelf(DeadlyQueen source, Entity target) {
        return new EntityBomb(source, When.PUSH_SWITCH, What.SELF, target);
    }

    public static EntityBomb pushSwitchTouching(DeadlyQueen source, Entity target) {
        return new EntityBomb(source, When.PUSH_SWITCH, What.TOUCHING_ENTITY, target);
    }

    public static EntityBomb touchSelf(DeadlyQueen source, Entity target) {
        return new EntityBomb(source, When.TOUCH, What.SELF, target);
    }

    public static EntityBomb touchTouching(DeadlyQueen source, Entity target) {
        return new EntityBomb(source, When.TOUCH, What.TOUCHING_ENTITY, target);
    }

    @Override
    public void tick() {
        if (this.explodeWhen == When.PUSH_SWITCH) {
            return;
        }

        var touching = this.level.getEntitiesOfClass(Entity.class, this.createAABB(), entity -> entity != this.target);
        if (this.whatExplodes == What.TOUCHING_ENTITY) {
            touching.removeIf(entity -> entity == this.stand || entity == this.stand.getOwner());
        }
        if (!touching.isEmpty()) {
            this.explode();
        }
    }

    @Override
    protected void explodeSelf() {
        this.level.explode(this.target, this.target.getX(), this.target.getY(), this.target.getZ(), 3.0F, ExplosionInteraction.NONE);
        this.target.discard();
    }

    @Override
    protected void explodeTouchingEntity() {
        this.level.getEntitiesOfClass(Entity.class, this.createAABB(), entity -> entity != this.target && entity != this.stand && entity != this.stand.getOwner()).forEach(entity -> {
            this.level.explode(entity, this.level.damageSources().explosion(entity, this.stand), new EntityExplosionDamageCalculator(), entity.position(), 3.0F, false, ExplosionInteraction.NONE);
            entity.discard();
        });
    }

    protected AABB createAABB() {
        return this.target.getBoundingBox();
    }

    public Entity getTarget() {
        return this.target;
    }

    protected class EntityExplosionDamageCalculator extends ExplosionDamageCalculator {
        @Override
        public boolean shouldBlockExplode(Explosion p_46094_, BlockGetter p_46095_, BlockPos p_46096_, BlockState p_46097_, float p_46098_) {
            return false;
        }

        @Override
        public boolean shouldDamageEntity(Explosion p_312772_, Entity p_311132_) {
            return !this.shouldExclude(p_311132_) && super.shouldDamageEntity(p_312772_, p_311132_);
        }

        @Override
        public float getEntityDamageAmount(Explosion p_310428_, Entity p_310135_) {
            return this.shouldExclude(p_310135_) ? 0.0F : super.getEntityDamageAmount(p_310428_, p_310135_);
        }

        public boolean shouldExclude(Entity entity) {
            if (entity == EntityBomb.this.stand.getOwner()) {
                return true;
            }

            return EntityBomb.this.whatExplodes == What.TOUCHING_ENTITY && entity == EntityBomb.this.target;
        }
    }
}
