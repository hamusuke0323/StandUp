package com.hamusuke.standup.stand.ability.deadly_queen.bomb;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityBomb extends Bomb {
    protected final Entity target;

    public EntityBomb(DeadlyQueen stand, When explodeWhen, What whatExplodes, Entity target) {
        super(stand, explodeWhen, whatExplodes);

        this.target = target;
    }

    @Override
    protected Vec3 getExplosionPos() {
        return this.target.position();
    }

    @Override
    protected AABB createAABB() {
        return this.target.getBoundingBox();
    }

    @Override
    protected boolean consideredTouching(Entity entity) {
        return entity != this.target && super.consideredTouching(entity);
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
    protected boolean fire() {
        return true;
    }

    public Entity getTarget() {
        return this.target;
    }
}
