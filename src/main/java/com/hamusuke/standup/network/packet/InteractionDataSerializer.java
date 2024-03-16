package com.hamusuke.standup.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class InteractionDataSerializer {
    private final At at;

    public InteractionDataSerializer(@Nullable HitResult result) {
        if (result == null || result.getType() == Type.MISS || (!(result instanceof BlockHitResult) && !(result instanceof EntityHitResult))) {
            this.at = new AtAir();
        } else if (result instanceof BlockHitResult blockHitResult) {
            this.at = new AtBlock(blockHitResult);
        } else {
            this.at = new AtEntity((EntityHitResult) result);
        }
    }

    public InteractionDataSerializer(FriendlyByteBuf buf) {
        var type = buf.readEnum(AtType.class);
        this.at = type.decoder.apply(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.at.getType());
        this.at.write(buf);
    }

    public void dispatch(Handler handler) {
        this.at.dispatch(handler);
    }

    private enum AtType {
        BLOCK(AtBlock::new),
        ENTITY(AtEntity::new),
        AIR(buf -> new AtAir());

        private final Function<FriendlyByteBuf, At> decoder;

        AtType(Function<FriendlyByteBuf, At> decoder) {
            this.decoder = decoder;
        }
    }

    private interface At {
        AtType getType();

        default void write(FriendlyByteBuf buf) {
        }

        void dispatch(Handler handler);
    }

    public interface Handler {
        void atBlock(BlockHitResult result);

        void atEntity(int id, Vec3 location);

        void atAir();
    }

    private record AtBlock(BlockHitResult result) implements At {
        private AtBlock(FriendlyByteBuf buf) {
            this(buf.readBlockHitResult());
        }

        @Override
        public AtType getType() {
            return AtType.BLOCK;
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeBlockHitResult(this.result);
        }

        @Override
        public void dispatch(Handler handler) {
            handler.atBlock(this.result);
        }
    }

    private static final class AtEntity implements At {
        private final int id;
        private final Vec3 location;

        private AtEntity(EntityHitResult result) {
            var entity = result.getEntity();
            while (entity instanceof PartEntity<?> part) {
                entity = part.getParent();
            }

            this.id = entity.getId();
            this.location = result.getLocation();
        }

        private AtEntity(FriendlyByteBuf buf) {
            this.id = buf.readVarInt();
            this.location = buf.readVec3();
        }

        @Override
        public AtType getType() {
            return AtType.ENTITY;
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(this.id);
            buf.writeVec3(this.location);
        }

        @Override
        public void dispatch(Handler handler) {
            handler.atEntity(this.id, this.location);
        }
    }

    private record AtAir() implements At {
        @Override
        public AtType getType() {
            return AtType.AIR;
        }

        @Override
        public void dispatch(Handler handler) {
            handler.atAir();
        }
    }
}
