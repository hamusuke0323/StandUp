package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.stand.Stand;
import com.hamusuke.standup.stand.Stand.StandOperationMode;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public class StandOperationModeToggleNotify implements Packet {
    private final int standId;
    private final StandOperationMode mode;

    public StandOperationModeToggleNotify(Stand stand, StandOperationMode mode) {
        this.standId = stand.getId();
        this.mode = mode;
    }

    public StandOperationModeToggleNotify(FriendlyByteBuf buf) {
        this.standId = buf.readVarInt();
        this.mode = buf.readEnum(StandOperationMode.class);
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
