package com.hamusuke.standup.mixin.client;

import com.hamusuke.standup.invoker.PlayerInvoker;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.c2s.StandMovementInputNotify;
import com.hamusuke.standup.network.packet.c2s.StandPosRotSyncNotify;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hamusuke.standup.util.MthH.SQRT_OF_THREE;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements PlayerInvoker {
    @Shadow
    public Input input;

    @Shadow
    public float yBobO;

    @Shadow
    public float xBobO;

    @Shadow
    public float yBob;

    @Shadow
    public float xBob;

    @Shadow
    private boolean crouching;

    @Shadow
    private float yRotLast;

    @Shadow
    private float xRotLast;

    @Shadow
    @Final
    public ClientPacketListener connection;

    public LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStep(CallbackInfo ci) {
        if (this.isControllingStand()) {
            this.crouching = false;
        }
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"))
    private void serverAiStep(CallbackInfo ci) {
        if (this.isControllingStand()) {
            this.xxa = 0.0F;
            this.zza = 0.0F;
            this.jumping = false;
            this.getStand().xxa = this.input.leftImpulse;
            this.getStand().zza = this.input.forwardImpulse;
            this.fallDistance = 0.0F;

            if (this.isControllingStand()) {
                this.yya = 0.0F;
                int j = 0;
                if (this.isShiftKeyDown()) {
                    --j;
                }

                if (this.input.jumping) {
                    ++j;
                }

                if (j != 0) {
                    this.getStand().setDeltaMovement(this.getStand().getDeltaMovement().add(0.0D, j * this.getStand().getFlyingSpeed(), 0.0D));
                }
            }

            if (this.getStand().isTooFarAway()) {
                double scale = this.getStand().position().distanceTo(this.position()) - this.getStand().maxMovableDistanceFromPlayer();
                scale *= SQRT_OF_THREE;
                this.getStand().setDeltaMovement(this.getStand().position().vectorTo(this.position()).normalize().scale(scale));
            }

            NetworkManager.sendToServer(new StandMovementInputNotify(this.getStand().xxa, this.getStand().zza, this.input.jumping, this.input.shiftKeyDown));

            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob += (this.getXRot() - this.xBob) * 0.5F;
            this.yBob += (this.getYRot() - this.yBob) * 0.5F;
        }
    }

    @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    private void sendPosition(CallbackInfo ci) {
        if (this.isControllingStand()) {
            double yd = this.getYRot() - this.yRotLast;
            double xd = this.getXRot() - this.xRotLast;
            if (yd != 0.0D || xd != 0.0D) {
                this.connection.send(new Rot(this.getStand().getYRot(), this.getStand().getXRot(), true));
                this.yRotLast = this.getYRot();
                this.xRotLast = this.getXRot();
            }

            NetworkManager.sendToServer(new StandPosRotSyncNotify(this.getStand().position(), this.getStand().getYRot(), this.getStand().getXRot(), this.isSprinting()));
        }
    }

    @Override
    public boolean canReach(BlockPos pos, double padding) {
        return (this.isControllingStand() && this.position().closerThan(pos.getCenter(), this.getStand().maxMovableDistanceFromPlayer())) || super.canReach(pos, padding);
    }

    @Override
    public boolean canReach(Entity entity, double padding) {
        return (this.isControllingStand() && this.position().closerThan(entity.position(), this.getStand().maxMovableDistanceFromPlayer())) || super.canReach(entity, padding);
    }

    @Override
    public boolean canReach(Vec3 entityHitVec, double padding) {
        return (this.isControllingStand() && this.position().closerThan(entityHitVec, this.getStand().maxMovableDistanceFromPlayer())) || super.canReach(entityHitVec, padding);
    }
}
