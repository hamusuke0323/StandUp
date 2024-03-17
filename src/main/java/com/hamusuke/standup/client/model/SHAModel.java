package com.hamusuke.standup.client.model;

import com.hamusuke.standup.stand.stands.SheerHeartAttack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SHAModel extends SilverfishModel<SheerHeartAttack> {
    public SHAModel(ModelPart p_170927_) {
        super(p_170927_);
    }

    @Override
    public void renderToBuffer(PoseStack p_170625_, VertexConsumer p_170626_, int p_170627_, int p_170628_, float p_170629_, float p_170630_, float p_170631_, float p_170632_) {
        super.renderToBuffer(p_170625_, p_170626_, p_170627_, p_170628_, p_170629_, p_170630_, p_170631_, 0.5F);
    }
}
