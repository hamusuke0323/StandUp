package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.config.Config;
import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

import static com.hamusuke.standup.StandUp.MOD_ID;

public record StandCardSetNotify(int ownerId, ItemStack card, boolean sendMsg) implements Packet {
    private static final Component PLEASE_STAND_UP_AGAIN = Component.translatable(MOD_ID + ".system.again");

    public StandCardSetNotify(Player owner, ItemStack card) {
        this(owner.getId(), card, true);
    }

    public StandCardSetNotify(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readItem(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.ownerId);
        buf.writeItem(this.card);
        buf.writeBoolean(this.sendMsg);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            if (mc.level.getEntity(this.ownerId) instanceof Player player && player instanceof PlayerInvoker invoker) {
                invoker.setStandCard(this.card);

                if (this.sendMsg && player == mc.player && Config.getClientConfig().setStandCardMsg.get()) {
                    player.sendSystemMessage(PLEASE_STAND_UP_AGAIN);
                }
            }
        }));

        return true;
    }
}
