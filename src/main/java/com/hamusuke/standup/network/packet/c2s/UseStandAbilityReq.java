package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record UseStandAbilityReq(int id) implements Packet {
    public UseStandAbilityReq(Stand stand) {
        this(stand.getId());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public boolean handle(Context context) {
        return Packet.super.handle(context);
    }
}
