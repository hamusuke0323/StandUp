package com.hamusuke.standup.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public interface Packet {
    default void write(FriendlyByteBuf buf) {
    }

    default boolean handle(Context context) {
        return false;
    }
}
