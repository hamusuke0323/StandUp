package com.hamusuke.standup.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hamusuke.standup.StandUp.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class CreateNewBombScreen extends Screen {
    private static final Component TITLE = Component.translatable(MOD_ID + ".screen.bomb");

    public CreateNewBombScreen() {
        super(TITLE);
    }

    private static Component createTargetDescription(HitResult result) {
        return Component.empty();
    }

    @Override
    protected void init() {

    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
    }


}
