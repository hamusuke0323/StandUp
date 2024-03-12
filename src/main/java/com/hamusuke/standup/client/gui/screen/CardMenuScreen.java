package com.hamusuke.standup.client.gui.screen;

import com.hamusuke.standup.world.inventory.StandCardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hamusuke.standup.StandUp.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class CardMenuScreen extends AbstractContainerScreen<StandCardMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/container/card_menu.png");

    public CardMenuScreen(StandCardMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int p_283661_, int p_281248_, float p_281886_) {
        super.render(guiGraphics, p_283661_, p_281248_, p_281886_);
        this.renderTooltip(guiGraphics, p_283661_, p_281248_);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
