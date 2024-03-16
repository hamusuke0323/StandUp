package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record DeadlyQueenWantsToKnowNewBombInfoRsp() implements Packet {
    public DeadlyQueenWantsToKnowNewBombInfoRsp(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public boolean handle(Context context) {
        return true;
    }
}
