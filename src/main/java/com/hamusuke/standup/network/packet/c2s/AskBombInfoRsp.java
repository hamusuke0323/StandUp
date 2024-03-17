package com.hamusuke.standup.network.packet.c2s;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.packet.InteractionDataSerializer;
import com.hamusuke.standup.network.packet.InteractionDataSerializer.Handler;
import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.BlockBomb;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.What;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.When;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.EntityBomb;
import com.hamusuke.standup.stand.stands.DeadlyQueen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import org.jetbrains.annotations.Nullable;

public record AskBombInfoRsp(InteractionDataSerializer serializer, When when,
                             What what) implements Packet {
    public AskBombInfoRsp(FriendlyByteBuf buf) {
        this(new InteractionDataSerializer(buf), buf.readEnum(When.class), buf.readEnum(What.class));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.serializer.write(buf);
        buf.writeEnum(this.when);
        buf.writeEnum(this.what);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> this.serializer.dispatch(new Handler() {
            @Nullable
            private DeadlyQueen getStand() {
                var sender = context.getSender();
                if (sender instanceof PlayerInvoker invoker && invoker.isStandAlive() && invoker.getStand() instanceof DeadlyQueen deadlyQueen) {
                    return deadlyQueen;
                }

                return null;
            }

            @Override
            public void atBlock(BlockHitResult result) {
                var stand = this.getStand();
                if (stand == null) {
                    return;
                }

                stand.placeBomb(new BlockBomb(stand, AskBombInfoRsp.this.when, AskBombInfoRsp.this.what, result.getBlockPos()));
            }

            @Override
            public void atEntity(int id, Vec3 location) {
                var entity = context.getSender().serverLevel().getEntity(id);
                var stand = this.getStand();
                if (entity == null || stand == null) {
                    return;
                }

                stand.placeBomb(new EntityBomb(stand, AskBombInfoRsp.this.when, AskBombInfoRsp.this.what, entity));
            }

            @Override
            public void atAir() {
            }
        }));

        return true;
    }
}
