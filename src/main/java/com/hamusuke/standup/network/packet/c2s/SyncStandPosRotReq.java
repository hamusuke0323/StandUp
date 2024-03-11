package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class SyncStandPosRotReq implements Packet {
    private final Vec3 pos;
    private final float yRot;
    private final float xRot;
    private final boolean sprinting;

    public SyncStandPosRotReq(Vec3 pos, float yRot, float xRot, boolean sprinting) {
        this.pos = pos;
        this.yRot = yRot;
        this.xRot = xRot;
        this.sprinting = sprinting;
    }

    public SyncStandPosRotReq(FriendlyByteBuf buf) {
        this.pos = buf.readVec3();
        this.yRot = buf.readFloat();
        this.xRot = buf.readFloat();
        this.sprinting = buf.readBoolean();
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
