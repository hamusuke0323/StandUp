package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record StandPosRotSyncNotify(Vec3 pos, float yRot, float xRot, boolean sprinting) implements Packet {
    public StandPosRotSyncNotify(FriendlyByteBuf buf) {
        this(buf.readVec3(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVec3(this.pos);
        buf.writeFloat(this.yRot);
        buf.writeFloat(this.xRot);
        buf.writeBoolean(this.sprinting);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() instanceof PlayerInvoker invoker && invoker.isStandAlive()) {
                invoker.getStand().setPos(this.pos);
                invoker.getStand().setYRot(this.yRot);
                invoker.getStand().setXRot(this.xRot);
                invoker.getStand().setSprinting(this.sprinting);
            }
        });

        return true;
    }
}
