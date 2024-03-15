package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.core.BlockPos;
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
    protected void explodeSelf() {
        var vec = this.blockPos.getCenter();
        this.level.explode(null, this.getSource(), this.createDamageCalculator(), vec.x(), vec.y(), vec.z(), this.getRadius(), this.fire(), this.getInteraction(), this.shouldAddParticle(), this.getSmallExplosionParticle(), this.getLargeExplosionParticle(), this.getExplosionSound());
    }

    @Override
    protected void explodeTouchingEntity() {
        this.level.getEntitiesOfClass(this.getType(), this.createAABB(), this::shouldExplode).forEach(entity -> {
            this.level.explode(entity, this.getSource(), this.createDamageCalculator(), entity.getX(), entity.getY(), entity.getZ(), this.getRadius(), this.fire(), this.getInteraction(), this.shouldAddParticle(), this.getSmallExplosionParticle(), this.getLargeExplosionParticle(), this.getExplosionSound());
            entity.discard();
        });
    }

    @Override
    protected AABB createAABB() {
        var shape = this.level.getBlockState(this.blockPos).getShape(this.level, this.blockPos);
        shape = shape.move(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
        return shape.isEmpty() ? new AABB(this.blockPos).inflate(0.1D) : shape.bounds().inflate(0.1D);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}
