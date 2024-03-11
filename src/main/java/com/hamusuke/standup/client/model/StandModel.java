package com.hamusuke.standup.client.model;

import com.hamusuke.standup.stand.Stand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class StandModel extends PlayerModel<Stand> {
    public StandModel(ModelPart p_170821_, boolean slim) {
        super(p_170821_, slim);
    }

    @Override
    public void renderToBuffer(PoseStack p_102034_, VertexConsumer p_102035_, int p_102036_, int p_102037_, float p_102038_, float p_102039_, float p_102040_, float p_102041_) {
        super.renderToBuffer(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, 0.5F);
    }
}
