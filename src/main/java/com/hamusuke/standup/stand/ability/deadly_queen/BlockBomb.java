package com.hamusuke.standup.stand.ability.deadly_queen;

import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlockBomb extends Bomb {
    protected final BlockPos blockPos;

    public BlockBomb(DeadlyQueen stand, When explodeWhen, What whatExplodes, BlockPos blockPos) {
        super(stand, explodeWhen, whatExplodes);

        this.blockPos = blockPos;
    }

    @Override
    protected void explodeSelf() {
        var vec = this.blockPos.getCenter();
        this.level.explode(null, this.getSource(), this.createDamageCalculator(), vec.x(), vec.y(), vec.z(), this.getRadius(), this.fire(), this.getInteraction(), this.getSmallExplosionParticle(), this.getLargeExplosionParticle(), this.getExplosionSound());
    }

    @Override
    protected void explodeTouchingEntity() {
        this.level.getEntitiesOfClass(this.getType(), this.createAABB(), this::shouldExplode).forEach(entity -> {
            this.level.explode(entity, this.getSource(), this.createDamageCalculator(), entity.getX(), entity.getY(), entity.getZ(), this.getRadius(), this.fire(), this.getInteraction(), this.getSmallExplosionParticle(), this.getLargeExplosionParticle(), this.getExplosionSound());
            entity.discard();
        });
    }

    @Override
    protected AABB createAABB() {
        var shape = this.level.getBlockState(this.blockPos).getShape(this.level, this.blockPos);
        shape = shape.move(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ());
        return shape.isEmpty() ? new AABB(this.blockPos).inflate(0.1D) : shape.bounds().inflate(0.1D);
    }

    @Override
    protected ParticleOptions getSmallExplosionParticle() {
        return ParticleTypes.LARGE_SMOKE;
    }

    @Override
    protected ExplosionDamageCalculator createDamageCalculator() {
        return new BombDamageCalculator(this) {
            @Override
            public boolean shouldBlockExplode(Explosion p_46094_, BlockGetter p_46095_, BlockPos p_46096_, BlockState p_46097_, float p_46098_) {
                if (BlockBomb.this.whatExplodes == What.SELF) {
                    return BlockBomb.this.blockPos.equals(p_46096_);
                }

                return super.shouldBlockExplode(p_46094_, p_46095_, p_46096_, p_46097_, p_46098_);
            }
        };
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}
