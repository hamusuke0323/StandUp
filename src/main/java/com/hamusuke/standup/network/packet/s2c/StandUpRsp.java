package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public record StandUpRsp(int standOwnerId, int standId) implements Packet {
    public StandUpRsp(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.standOwnerId);
        buf.writeVarInt(this.standId);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var level = Minecraft.getInstance().level;

            if (level != null && level.getEntity(this.standOwnerId) instanceof PlayerInvoker owner && level.getEntity(this.standId) instanceof Stand stand) {
                owner.standUp(stand);
            }
        }));

        return true;
    }
}
