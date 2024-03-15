package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
    protected void explodeSelf() {
        this.level.explode(this.target, this.getSource(), this.createDamageCalculator(), this.target.getX(), this.target.getY(), this.target.getZ(), this.getRadius(), this.fire(), this.getInteraction(), this.shouldAddParticle(), this.getSmallExplosionParticle(), this.getLargeExplosionParticle(), this.getExplosionSound());
        this.target.discard();
    }

    @Override
    protected void explodeTouchingEntity() {
        this.level.getEntitiesOfClass(Entity.class, this.createAABB(), this::shouldExplode).forEach(entity -> {
            this.level.explode(entity, this.getSource(), this.createDamageCalculator(), entity.getX(), entity.getY(), entity.getZ(), this.getRadius(), this.fire(), this.getInteraction(), this.shouldAddParticle(), this.getSmallExplosionParticle(), this.getLargeExplosionParticle(), this.getExplosionSound());
            entity.discard();
        });
    }

    @Override
    protected AABB createAABB() {
        return this.target.getBoundingBox();
    }

    @Override
    protected boolean shouldExplode(Entity entity) {
        return switch (this.whatExplodes) {
            case SELF -> super.shouldExplode(entity);
            case TOUCHING_ENTITY -> entity != this.target && super.shouldExplode(entity);
        };
    }

    @Override
    protected float getRadius() {
        return 3.0F;
    }

    @Override
    protected DamageSource getSource() {
        return this.level.damageSources().explosion(this.target, this.stand.getOwner());
    }

    @Override
    protected boolean fire() {
        return true;
    }

    public Entity getTarget() {
        return this.target;
    }
}
