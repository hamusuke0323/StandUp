package com.hamusuke.standup.network.packet.s2c;

import com.hamusuke.standup.client.gui.screen.CreateNewBombScreen;
import com.hamusuke.standup.network.packet.InteractionDataSerializer;
import com.hamusuke.standup.network.packet.InteractionDataSerializer.Handler;
import com.hamusuke.standup.network.packet.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;

public record AskBombInfoReq(InteractionDataSerializer serializer) implements Packet {
    public AskBombInfoReq(HitResult result) {
        this(new InteractionDataSerializer(result));
    }

    public AskBombInfoReq(FriendlyByteBuf buf) {
        this(new InteractionDataSerializer(buf));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.serializer.write(buf);
    }

    @Override
    public boolean handle(Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.serializer.dispatch(new Handler() {
            private static final Minecraft mc = Minecraft.getInstance();

            @Override
            public void atBlock(BlockHitResult result) {
                mc.setScreen(new CreateNewBombScreen(result));
            }

            @Override
            public void atEntity(int id, Vec3 location) {
                var entity = mc.level.getEntity(id);
                if (entity == null) {
                    return;
                }

                mc.setScreen(new CreateNewBombScreen(new EntityHitResult(entity, location)));
            }

            @Override
            public void atAir() {
            }
        })));

        return true;
    }
}
