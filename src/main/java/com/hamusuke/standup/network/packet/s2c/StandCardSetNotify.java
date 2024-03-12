package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.CommonConfig;
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

public class StandCardSetNotify implements Packet {
    private static final Component PLEASE_STAND_UP_AGAIN = Component.translatable(MOD_ID + ".system.again");
    private final int ownerId;
    private final ItemStack card;

    public StandCardSetNotify(Player owner, ItemStack card) {
        this.ownerId = owner.getId();
        this.card = card;
    }

    public StandCardSetNotify(FriendlyByteBuf buf) {
        this.ownerId = buf.readVarInt();
        this.card = buf.readItem();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.ownerId);
        buf.writeItem(this.card);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            var mc = Minecraft.getInstance();
            if (mc.level.getEntity(this.ownerId) instanceof Player player && player instanceof PlayerInvoker invoker) {
                invoker.setStandCard(this.card);

                if (player == mc.player && CommonConfig.setStandCardMsg) {
                    player.sendSystemMessage(PLEASE_STAND_UP_AGAIN);
                }
            }
        }));

        return true;
    }
}
