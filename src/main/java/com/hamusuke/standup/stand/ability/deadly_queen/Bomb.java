package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;

public abstract class Bomb {
    protected final DeadlyQueen stand;
    protected final ServerLevel level;
    protected final When explodeWhen;
    protected final What whatExplodes;
    protected boolean exploded;

    protected Bomb(DeadlyQueen stand, When explodeWhen, What whatExplodes) {
        this.stand = stand;
        this.level = (ServerLevel) stand.level();
        this.explodeWhen = explodeWhen;
        this.whatExplodes = whatExplodes;
    }

    public void tick() {
        if (this.explodeWhen == When.PUSH_SWITCH) {
            return;
        }

        var touchingEntities = this.level.getEntitiesOfClass(this.getType(), this.createAABB(), this::shouldExplode);
        if (touchingEntities.isEmpty()) {
            return;
        }

        this.explode();
    }

    public void ignite() {
        if (this.explodeWhen == When.PUSH_SWITCH) {
            this.explode();
        }
    }

    protected void explode() {
        this.exploded = true;

        switch (this.whatExplodes) {
            case SELF -> this.explodeSelf();
            case TOUCHING_ENTITY -> this.explodeTouchingEntity();
        }
    }

    protected abstract void explodeSelf();

    protected abstract void explodeTouchingEntity();

    protected abstract AABB createAABB();

    protected DamageSource getSource() {
        return this.level.damageSources().explosion(this.stand, this.stand.getOwner());
    }

    protected float getRadius() {
        return 1.0F;
    }

    protected boolean fire() {
        return false;
    }

    protected ExplosionInteraction getInteraction() {
        return ExplosionInteraction.NONE;
    }

    protected boolean shouldAddParticle() {
        return true;
    }

    protected ParticleOptions getSmallExplosionParticle() {
        return ParticleTypes.EXPLOSION;
    }

    protected ParticleOptions getLargeExplosionParticle() {
        return ParticleTypes.EXPLOSION_EMITTER;
    }

    protected SoundEvent getExplosionSound() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    protected ExplosionDamageCalculator createDamageCalculator() {
        return new BombDamageCalculator(this);
    }

    protected <T extends Entity> Class<T> getType() {
        return (Class<T>) Entity.class;
    }

    protected boolean shouldExplode(Entity entity) {
        return entity != this.stand && entity != this.stand.getOwner();
    }

    public boolean isStillValid() {
        return !this.exploded;
    }

    public enum When {
        PUSH_SWITCH,
        TOUCH
    }

    public enum What {
        SELF,
        TOUCHING_ENTITY
    }

    protected static class BombDamageCalculator extends ExplosionDamageCalculator {
        private final Bomb bomb;

        public BombDamageCalculator(Bomb bomb) {
            this.bomb = bomb;
        }

        @Override
        public boolean shouldDamageEntity(Explosion p_312772_, Entity p_311132_) {
            return this.bomb.shouldExplode(p_311132_);
        }

        @Override
        public float getEntityDamageAmount(Explosion p_310428_, Entity p_310135_) {
            return this.bomb.shouldExplode(p_310135_) ? super.getEntityDamageAmount(p_310428_, p_310135_) : 0.0F;
        }
    }
}
