package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record StandDownReq() implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var invoker = PlayerInvoker.invoker(context.getSender());
            if (invoker != null) {
                invoker.standDown();
            }
        });

        return true;
    }
}
