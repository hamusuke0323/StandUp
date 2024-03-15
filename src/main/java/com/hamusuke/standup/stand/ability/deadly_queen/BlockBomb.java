package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;

public class BlockBomb extends Bomb {
    protected final BlockPos blockPos;

    protected BlockBomb(DeadlyQueen stand, When explodeWhen, What whatExplodes, BlockPos blockPos) {
        super(stand, explodeWhen, whatExplodes);

        this.blockPos = blockPos;
    }

    public static BlockBomb pushSwitchSelf(DeadlyQueen source, BlockPos blockPos) {
        return new BlockBomb(source, When.PUSH_SWITCH, What.SELF, blockPos);
    }

    public static BlockBomb pushSwitchTouching(DeadlyQueen source, BlockPos blockPos) {
        return new BlockBomb(source, When.PUSH_SWITCH, What.TOUCHING_ENTITY, blockPos);
    }

    public static BlockBomb touchSelf(DeadlyQueen source, BlockPos blockPos) {
        return new BlockBomb(source, When.TOUCH, What.SELF, blockPos);
    }

    public static BlockBomb touchTouching(DeadlyQueen source, BlockPos blockPos) {
        return new BlockBomb(source, When.TOUCH, What.TOUCHING_ENTITY, blockPos);
    }

    @Override
    public void tick() {
        if (this.explodeWhen == When.PUSH_SWITCH) {
            return;
        }

        var touchingEntities = this.level.getEntitiesOfClass(Entity.class, this.createAABB(), entity -> entity != this.stand && entity != this.stand.getOwner());
        if (!touchingEntities.isEmpty()) {
            this.explode();
        }
    }

    @Override
    protected void explodeSelf() {
        var center = this.blockPos.getCenter();
        this.level.explode(this.stand, center.x, center.y, center.z, 1.0F, false, ExplosionInteraction.NONE);
    }

    @Override
    protected void explodeTouchingEntity() {
        this.level.getEntitiesOfClass(Entity.class, this.createAABB(), entity -> {
            return entity != this.stand && entity != this.stand.getOwner();
        }).forEach(entity -> {
            this.level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), 1.0F, ExplosionInteraction.NONE);
            entity.discard();
        });
    }

    protected AABB createAABB() {
        var shape = this.level.getBlockState(this.blockPos).getShape(this.level, this.blockPos);
        shape = shape.move(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
        return shape.isEmpty() ? new AABB(this.blockPos).inflate(0.1D) : shape.bounds().inflate(0.1D);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}
