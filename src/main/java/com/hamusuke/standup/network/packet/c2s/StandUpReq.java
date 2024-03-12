package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.world.item.StandCardItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class StandUpReq implements Packet {
    private final boolean slim;

    public StandUpReq(boolean slim) {
        this.slim = slim;
    }

    public StandUpReq(FriendlyByteBuf buf) {
        this.slim = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.slim);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> {
            var sender = context.getSender();
            var invoker = PlayerInvoker.invoker(sender);

            if (invoker.isStandAlive() || sender.isSpectator()) {
                return;
            }

            sender.serverLevel().getAllEntities().forEach(entity -> {
                if (entity instanceof Stand stand && (stand.getOwner() == null || stand.getOwner() == sender)) {
                    stand.remove(RemovalReason.DISCARDED);
                }
            });

            var ability = StandCardItem.getStandCardFrom(invoker.getStandCard());
            var stand = ability.createStand(sender.level(), sender, this.slim);
            stand.setPos(sender.getEyePosition().add(0.1D, 0.02D, 0.1D));
            sender.level().addFreshEntity(stand);
        });

        return true;
    }
}
