package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record StandRotateHeadNotify(float yHeadRot) implements Packet {
    public StandRotateHeadNotify(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.yHeadRot);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var sender = context.getSender();
            if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                invoker.getStand().setYHeadRot(this.yHeadRot);
            }
        });

        return true;
    }
}
