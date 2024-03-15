package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import org.jetbrains.annotations.Nullable;

public class UseStandAbilityReq implements Packet {
    @Nullable
    private final HitResult result;
    private final int entityId;
    private final Vec3 location;

    public UseStandAbilityReq(@Nullable HitResult result) {
        this.result = result;
        int id = -1;
        Vec3 location = Vec3.ZERO;
        if (this.result instanceof EntityHitResult entity) {
            id = entity.getEntity().getId();
            location = entity.getLocation();
        }

        this.entityId = id;
        this.location = location;
    }

    public UseStandAbilityReq(FriendlyByteBuf buf) {
        switch (buf.readEnum(Type.class)) {
            case BLOCK -> {
                this.result = buf.readBlockHitResult();
                this.entityId = -1;
                this.location = Vec3.ZERO;
            }
            case ENTITY -> {
                this.result = null;
                this.entityId = buf.readVarInt();
                this.location = buf.readVec3();
            }
            default -> {
                this.result = null;
                this.entityId = -1;
                this.location = Vec3.ZERO;
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.result == null ? Type.MISS : this.result.getType());
        if (this.result == null || this.result.getType() == Type.MISS) {
            return;
        }

        if (this.result instanceof BlockHitResult blockHitResult) {
            buf.writeBlockHitResult(blockHitResult);
        } else if (this.result instanceof EntityHitResult) {
            buf.writeVarInt(this.entityId);
            buf.writeVec3(this.location);
        }
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var sender = context.getSender();
            if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                if (this.result instanceof BlockHitResult hitResult) {
                    invoker.getStand().onInteractAtBlock(hitResult, true);
                } else if (this.entityId >= 0) {
                    var entity = sender.level().getEntity(this.entityId);
                    if (entity != null) {
                        invoker.getStand().onInteractAt(new EntityHitResult(entity, this.location), true);
                    }
                } else {
                    invoker.getStand().onInteractAtAir();
                }
            }
        });

        return true;
    }
}
