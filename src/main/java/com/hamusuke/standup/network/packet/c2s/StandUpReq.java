package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.stands.Stand;
import com.hamusuke.standup.util.MthH;
import com.hamusuke.standup.world.item.StandCardItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public record StandUpReq(boolean slim) implements Packet {
    public StandUpReq(FriendlyByteBuf buf) {
        this(buf.readBoolean());
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
                if (entity instanceof Stand stand && stand.getOwner() == sender) {
                    stand.discard();
                }
            });

            var ability = StandCardItem.getStandCardFrom(invoker.getStandCard());
            var stand = ability.createStand(sender.level(), sender, this.slim);
            var vec = MthH.behindVector(sender);
            stand.setPos(sender.position().add(vec.scale(0.5D)));
            sender.level().addFreshEntity(stand);
        });

        return true;
    }
}
