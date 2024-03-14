package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.stands.Stand;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public record HoldOrReleaseStandOwnerNotify(int standId, boolean hold) implements Packet {
    public HoldOrReleaseStandOwnerNotify(Stand stand, boolean hold) {
        this(stand.getId(), hold);
    }

    public HoldOrReleaseStandOwnerNotify(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.standId);
        buf.writeBoolean(this.hold);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            if (mc.level.getEntity(this.standId) instanceof Stand stand) {
                if (this.hold) {
                    stand.startHoldingOwner();
                } else {
                    stand.stopHoldingOwner();
                }
            }
        }));

        return true;
    }
}
