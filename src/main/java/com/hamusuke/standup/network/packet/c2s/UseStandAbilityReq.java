package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.InteractionDataSerializer;
import com.hamusuke.standup.network.packet.InteractionDataSerializer.Handler;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import org.jetbrains.annotations.Nullable;

public record UseStandAbilityReq(InteractionDataSerializer serializer) implements Packet {
    public UseStandAbilityReq(@Nullable HitResult result) {
        this(new InteractionDataSerializer(result));
    }

    public UseStandAbilityReq(FriendlyByteBuf buf) {
        this(new InteractionDataSerializer(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.serializer.write(buf);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> this.serializer.dispatch(new Handler() {
            @Override
            public void atBlock(BlockHitResult result) {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                    invoker.getStand().onInteractAtBlock(result, true);
                }
            }

            @Override
            public void atEntity(int id, Vec3 location) {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                    var entity = sender.level().getEntity(id);
                    if (entity == null) {
                        return;
                    }

                    invoker.getStand().onInteractAt(new EntityHitResult(entity, location), true);
                }
            }

            @Override
            public void atAir() {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isControllingStand()) {
                    invoker.getStand().onInteractAtAir();
                }
            }
        }));

        return true;
    }
}
