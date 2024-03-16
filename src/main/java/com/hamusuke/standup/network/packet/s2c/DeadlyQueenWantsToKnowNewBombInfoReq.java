package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public record DeadlyQueenWantsToKnowNewBombInfoReq() implements Packet {
    public DeadlyQueenWantsToKnowNewBombInfoReq(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
        }));

        return true;
    }
}
