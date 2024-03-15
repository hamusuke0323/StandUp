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
        MAIN.messageBuilder(HoldOrReleaseOwnerReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new HoldOrReleaseOwnerReq()).consumerNetworkThread(HoldOrReleaseOwnerReq::handle).add();
        MAIN.messageBuilder(StandDownReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new StandDownReq()).consumerNetworkThread(StandDownReq::handle).add();
        MAIN.messageBuilder(StandMovementInputReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(StandMovementInputReq::new).consumerNetworkThread(StandMovementInputReq::handle).add();
        MAIN.messageBuilder(SyncStandPosRotReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(SyncStandPosRotReq::new).consumerNetworkThread(SyncStandPosRotReq::handle).add();
        MAIN.messageBuilder(StandOperationModeToggleReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(buf -> new StandOperationModeToggleReq()).consumerNetworkThread(StandOperationModeToggleReq::handle).add();
        MAIN.messageBuilder(StandUpReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(StandUpReq::new).consumerNetworkThread(StandUpReq::handle).add();
        MAIN.messageBuilder(UseStandAbilityReq.class, ID.get(), NetworkDirection.PLAY_TO_SERVER).encoder(Packet::write).decoder(UseStandAbilityReq::new).consumerNetworkThread(UseStandAbilityReq::handle).add();
    }

    private static void registerS2CPackets() {
        MAIN.messageBuilder(HoldOrReleaseStandOwnerNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(HoldOrReleaseStandOwnerNotify::new).consumerNetworkThread(HoldOrReleaseStandOwnerNotify::handle).add();
        MAIN.messageBuilder(StandAppearNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(StandAppearNotify::new).consumerNetworkThread(StandAppearNotify::handle).add();
        MAIN.messageBuilder(StandCardSetNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(StandCardSetNotify::new).consumerNetworkThread(StandCardSetNotify::handle).add();
        MAIN.messageBuilder(StandDisappearNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(StandDisappearNotify::new).consumerNetworkThread(StandDisappearNotify::handle).add();
        MAIN.messageBuilder(StandOperationModeToggleNotify.class, ID.get(), NetworkDirection.PLAY_TO_CLIENT).encoder(Packet::write).decoder(StandOperationModeToggleNotify::new).consumerNetworkThread(StandOperationModeToggleNotify::handle).add();
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
