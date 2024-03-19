package com.hamusuke.standup.stand.ability.deadly_queen.bomb;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockBomb extends Bomb {
    protected final BlockPos blockPos;

    public BlockBomb(DeadlyQueen stand, When explodeWhen, What whatExplodes, BlockPos blockPos) {
        super(stand, explodeWhen, whatExplodes);
        this.blockPos = blockPos;
    }

    @Override
    protected void explodeSelf() {
        super.explodeSelf();
        this.level.removeBlock(this.blockPos, true);
    }

    @Override
    protected Vec3 getExplosionPos() {
        return this.blockPos.getCenter();
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
