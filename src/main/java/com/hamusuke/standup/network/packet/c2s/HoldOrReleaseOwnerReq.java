package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record HoldOrReleaseOwnerReq() implements Packet {
    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            if (context.getSender() instanceof PlayerInvoker invoker && invoker.isStandAlive()) {
                if (invoker.getStand().isHoldingOwner() && !context.getSender().isPassenger()) {
                    invoker.getStand().stopHoldingOwner();
                } else {
                    invoker.getStand().startHoldingOwner();
                }
            }
        });

        return true;
    }
}
