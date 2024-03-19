package com.hamusuke.standup.network;

import com.hamusuke.standup.StandUp;
import com.hamusuke.standup.network.packet.Packet;
import com.hamusuke.standup.network.packet.c2s.*;
import com.hamusuke.standup.network.packet.s2c.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class NetworkManager {
    private static final SimpleChannel MAIN = ChannelBuilder.named(new ResourceLocation(StandUp.MOD_ID, "main")).networkProtocolVersion(1).simpleChannel();
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final Supplier<Integer> ID = COUNTER::incrementAndGet;

    public static void registerPackets() {
        registerC2SPackets();
        registerS2CPackets();
    }

    private static void registerC2SPackets() {
        MAIN.messageBuilder(AskBombInfoRsp.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(AskBombInfoRsp::new)
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(HoldOrReleaseOwnerReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(buf -> new HoldOrReleaseOwnerReq())
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandCardMenuOpenReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(buf -> new StandCardMenuOpenReq())
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandDownReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(buf -> new StandDownReq())
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandMovementInputNotify.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(StandMovementInputNotify::new)
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandOpModeToggleReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(buf -> new StandOpModeToggleReq())
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandPosRotSyncNotify.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(StandPosRotSyncNotify::new)
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandRotateHeadNotify.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(StandRotateHeadNotify::new)
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(StandUpReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(StandUpReq::new)
                .consumerNetworkThread(Packet::handle)
                .add();
        MAIN.messageBuilder(UseStandAbilityNotify.class, ID.get(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(Packet::write)
                .decoder(UseStandAbilityNotify::new)
                .consumerNetworkThread(Packet::handle)
                .add();
    }

    private static void registerS2CPackets() {
        MAIN.messageBuilder(AskBombInfoReq.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(AskBombInfoReq::new)
                .consumerNetworkThread(AskBombInfoReq::handle)
                .add();
        MAIN.messageBuilder(HoldOrReleaseOwnerNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(HoldOrReleaseOwnerNotify::new)
                .consumerNetworkThread(HoldOrReleaseOwnerNotify::handle)
                .add();
        MAIN.messageBuilder(StandCardSetNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(StandCardSetNotify::new)
                .consumerNetworkThread(StandCardSetNotify::handle)
                .add();
        MAIN.messageBuilder(StandDownRsp.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(StandDownRsp::new)
                .consumerNetworkThread(StandDownRsp::handle)
                .add();
        MAIN.messageBuilder(StandOpModeToggleNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(StandOpModeToggleNotify::new)
                .consumerNetworkThread(StandOpModeToggleNotify::handle)
                .add();
        MAIN.messageBuilder(StandUpRsp.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(Packet::write)
                .decoder(StandUpRsp::new)
                .consumerNetworkThread(StandUpRsp::handle)
                .add();
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(Packet packet) {
        var mc = Minecraft.getInstance();
        MAIN.send(packet, Objects.requireNonNull(mc.getConnection()).getConnection());
    }

    public static void sendToClient(Packet packet, ServerPlayer serverPlayer) {
        if (serverPlayer == null || serverPlayer.connection == null) {
            return;
        }

        MAIN.send(packet, serverPlayer.connection.getConnection());
    }
}
