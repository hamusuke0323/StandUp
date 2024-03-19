package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

import static com.hamusuke.standup.StandUp.MOD_ID;

public record StandCardMenuOpenReq() implements Packet {
    private static final Component PLEASE_STAND_UP = Component.translatable(MOD_ID + ".system.open.menu");

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var sender = context.getSender();
            if (sender instanceof PlayerInvoker invoker) {
                if (!invoker.isStandAlive()) {
                    sender.sendSystemMessage(PLEASE_STAND_UP, true);
                    return;
                }

                sender.openMenu(invoker.getStand());
            }
        });

        return true;
    }
}
