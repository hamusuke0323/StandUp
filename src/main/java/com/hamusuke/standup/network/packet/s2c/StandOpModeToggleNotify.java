package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.stand.stands.Stand.StandOperationMode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public record StandOpModeToggleNotify(int standId, StandOperationMode mode) implements Packet {
    public StandOpModeToggleNotify(Stand stand, StandOperationMode mode) {
        this(stand.getId(), mode);
    }

    public StandOpModeToggleNotify(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readEnum(StandOperationMode.class));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.standId);
        buf.writeEnum(this.mode);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().player.level().getEntity(this.standId) instanceof Stand stand) {
                stand.setMode(this.mode);
            }
        }));

        return true;
    }
}
