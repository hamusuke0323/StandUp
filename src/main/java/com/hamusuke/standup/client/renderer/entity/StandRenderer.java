package com.hamusuke.standup.client.renderer.entity;

import com.hamusuke.standup.client.model.StandModel;
import com.hamusuke.standup.stand.stands.Stand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

@OnlyIn(Dist.CLIENT)
public class StandRenderer extends HumanoidMobRenderer<Stand, StandModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public StandRenderer(Context context, boolean slim) {
        super(context, new StandModel(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5F);
    }

    @Override
    public void render(Stand stand, float p_115456_, float partialTicks, PoseStack poseStack, MultiBufferSource source, int i) {
        this.setModelProperties(stand);
        super.render(stand, p_115456_, partialTicks, poseStack, source, i);
    }

    @Override
    protected boolean isBodyVisible(Stand p_115341_) {
        return !p_115341_.isInvisibleTo(Minecraft.getInstance().player);
    }

    private void setModelProperties(Stand stand) {
        if (stand.getOwner() instanceof AbstractClientPlayer player) {
            boolean following = stand.isFollowingOwner();
            var model = this.getModel();
            model.setAllVisible(true);

            model.hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
            model.jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
            model.leftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            model.rightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            model.leftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            model.rightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            model.crouching = following ? player.isCrouching() : stand.isCrouching();
            var pose = stand.isAggressive() ? ArmPose.EMPTY : getArmPose(player, InteractionHand.MAIN_HAND);
            var pose1 = stand.isAggressive() ? ArmPose.EMPTY : getArmPose(player, InteractionHand.OFF_HAND);
            if (pose.isTwoHanded()) {
                pose1 = player.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
            }

            if (player.getMainArm() == HumanoidArm.RIGHT) {
                model.rightArmPose = pose;
                model.leftArmPose = pose1;
            } else {
                model.rightArmPose = pose1;
                model.leftArmPose = pose;
            }
        }
    }

    @Override
    protected void setupRotations(Stand stand, PoseStack stack, float p_115319_, float p_115320_, float p_115321_) {
        if (stand.getOwner() instanceof AbstractClientPlayer player) {
            var living = stand.isFollowingOwner() ? player : stand;

            float f = living.getSwimAmount(p_115321_);
            float f1 = living.getViewXRot(p_115321_);
            float f4;
            float f3;
            if (living.isFallFlying()) {
                super.setupRotations(stand, stack, p_115319_, p_115320_, p_115321_);
                f4 = (float) living.getFallFlyingTicks() + p_115321_;
                f3 = Mth.clamp(f4 * f4 / 100.0F, 0.0F, 1.0F);
                if (!living.isAutoSpinAttack()) {
                    stack.mulPose(Axis.XP.rotationDegrees(f3 * (-90.0F - f1)));
                }

                var vec3 = living.getViewVector(p_115321_);
                var vec31 = player.getDeltaMovementLerped(p_115321_);
                double d0 = vec31.horizontalDistanceSqr();
                double d1 = vec3.horizontalDistanceSqr();
                if (d0 > 0.0 && d1 > 0.0) {
                    double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                    double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                    stack.mulPose(Axis.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
                }
            } else if (f > 0.0F) {
                super.setupRotations(stand, stack, p_115319_, p_115320_, p_115321_);
                f4 = !living.isInWater() && !living.isInFluidType((fluidType, height) -> living.canSwimInFluidType(fluidType)) ? -90.0F : -90.0F - living.getXRot();
                f3 = Mth.lerp(f, 0.0F, f4);
                stack.mulPose(Axis.XP.rotationDegrees(f3));
                if (living.isVisuallySwimming()) {
                    stack.translate(0.0F, -1.0F, 0.3F);
                }
            } else {
                super.setupRotations(stand, stack, p_115319_, p_115320_, p_115321_);
            }
        } else {
            super.setupRotations(stand, stack, p_115319_, p_115320_, p_115321_);
        }
    }

    @Override
    protected void scale(Stand p_115314_, PoseStack p_115315_, float p_115316_) {
        this.shadowRadius = 0.0F;
        this.shadowStrength = 0.0F;

        p_115315_.scale(0.9375F, 0.9375F, 0.9375F);
    }

    private static ArmPose getArmPose(LivingEntity living, InteractionHand hand) {
        var itemstack = living.getItemInHand(hand);
        if (itemstack.isEmpty()) {
            return ArmPose.EMPTY;
        } else {
            if (living.getUsedItemHand() == hand && living.getUseItemRemainingTicks() > 0) {
                var useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK) {
                    return ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW) {
                    return ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR) {
                    return ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && hand == living.getUsedItemHand()) {
                    return ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS) {
                    return ArmPose.SPYGLASS;
                }

                if (useanim == UseAnim.TOOT_HORN) {
                    return ArmPose.TOOT_HORN;
                }

                if (useanim == UseAnim.BRUSH) {
                    return ArmPose.BRUSH;
                }
            } else if (!living.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return ArmPose.CROSSBOW_HOLD;
            }

            var forgeArmPose = IClientItemExtensions.of(itemstack).getArmPose(living, hand, itemstack);
            return forgeArmPose != null ? forgeArmPose : ArmPose.ITEM;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Stand stand) {
        var texture = stand.getStandCard().getStandTexture();
        if (texture != null) {
            return texture;
        }

        if (stand.getOwner() instanceof AbstractClientPlayer player) {
            return player.getSkin().texture();
        }

        return TEXTURE;
    }
}
