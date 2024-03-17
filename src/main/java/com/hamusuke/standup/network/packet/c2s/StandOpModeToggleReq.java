package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record StandOpModeToggleReq() implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var sender = context.getSender();
            if (sender instanceof PlayerInvoker invoker && invoker.isStandAlive()) {
                invoker.getStand().toggleMode();
            }
        });

        return true;
    }
}
