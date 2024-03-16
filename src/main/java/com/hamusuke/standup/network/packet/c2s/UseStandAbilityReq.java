package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class UseStandAbilityReq implements Packet {
    private final At at;

    public UseStandAbilityReq(@Nullable HitResult result) {
        if (result == null || result.getType() == Type.MISS || (!(result instanceof BlockHitResult) && !(result instanceof EntityHitResult))) {
            this.at = new AtAir();
        } else if (result instanceof BlockHitResult blockHitResult) {
            this.at = new AtBlock(blockHitResult);
        } else {
            this.at = new AtEntity((EntityHitResult) result);
        }
    }

    public UseStandAbilityReq(FriendlyByteBuf buf) {
        var type = buf.readEnum(AtType.class);
        this.at = type.decoder.apply(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.at.getType());
        this.at.write(buf);
    }

    @Override
    public boolean handle(Context context) {
        return this.at.handle(context);
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

    private interface At extends Packet {
        AtType getType();
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
        public boolean handle(Context context) {
            context.enqueueWork(() -> {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                    invoker.getStand().onInteractAtBlock(this.result, true);
                }
            });

            return true;
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
        public boolean handle(Context context) {
            context.enqueueWork(() -> {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                    var entity = sender.level().getEntity(this.id);
                    if (entity == null) {
                        return;
                    }

                    invoker.getStand().onInteractAt(new EntityHitResult(entity, this.location), true);
                }
            });

            return true;
        }
    }

    private record AtAir() implements At {
        @Override
        public AtType getType() {
            return AtType.AIR;
        }

        @Override
        public boolean handle(Context context) {
            context.enqueueWork(() -> {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                    invoker.getStand().onInteractAtAir();
                }
            });

            return true;
        }
    }
}
