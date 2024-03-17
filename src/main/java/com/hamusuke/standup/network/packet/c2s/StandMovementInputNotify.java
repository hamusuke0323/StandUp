package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record StandMovementInputNotify(float xxa, float zza, boolean jumping, boolean shiftKeyDown) implements Packet {
    public StandMovementInputNotify(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.xxa);
        buf.writeFloat(this.zza);
        buf.writeBoolean(this.jumping);
        buf.writeBoolean(this.shiftKeyDown);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                invoker.getStand().xxa = this.xxa;
                invoker.getStand().zza = this.zza;
                invoker.getStand().setJumping(this.jumping);
                invoker.getStand().setShiftKeyDown(this.shiftKeyDown);
            }
        });

        return true;
    }
}
