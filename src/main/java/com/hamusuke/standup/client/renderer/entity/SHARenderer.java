package com.hamusuke.standup.client.renderer.entity;

import com.hamusuke.standup.client.model.SHAModel;
import com.hamusuke.standup.stand.stands.SheerHeartAttack;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hamusuke.standup.StandUp.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class SHARenderer extends MobRenderer<SheerHeartAttack, SHAModel> {
    private static final ResourceLocation SHA_LOCATION = new ResourceLocation(MOD_ID, "textures/stand/sha.png");

    public SHARenderer(Context p_174378_) {
        super(p_174378_, new SHAModel(p_174378_.bakeLayer(ModelLayers.SILVERFISH)), 0.0F);
    }

    @Override
    protected float getFlipDegrees(SheerHeartAttack p_115337_) {
        return 180.0F;
    }

    @Override
    protected void scale(SheerHeartAttack p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(SheerHeartAttack sheerHeartAttack) {
        return SHA_LOCATION;
    }
}
