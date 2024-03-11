package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class StandMovementInputReq implements Packet {
    private final float xxa;
    private final float zza;
    private final boolean jumping;
    private final boolean shiftKeyDown;

    public StandMovementInputReq(float xxa, float zza, boolean jumping, boolean shiftKeyDown) {
        this.xxa = xxa;
        this.zza = zza;
        this.jumping = jumping;
        this.shiftKeyDown = shiftKeyDown;
    }

    public StandMovementInputReq(FriendlyByteBuf buf) {
        this.xxa = buf.readFloat();
        this.zza = buf.readFloat();
        this.jumping = buf.readBoolean();
        this.shiftKeyDown = buf.readBoolean();
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
