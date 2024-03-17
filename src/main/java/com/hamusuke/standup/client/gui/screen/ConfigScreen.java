package com.hamusuke.standup.client.gui.screen;

import com.hamusuke.standup.client.gui.component.ComponentList;
import com.hamusuke.standup.config.ClientConfig;
import com.hamusuke.standup.config.CommonConfig;
import com.hamusuke.standup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hamusuke.standup.StandUp.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable(MOD_ID + ".config.title");
    private static final Component STAND_CAN_OPEN_DOOR = Component.translatable(MOD_ID + ".config.canOpenDoor");
    private static final Component STAND_CAN_OPEN_GATE = Component.translatable(MOD_ID + ".config.canOpenGate");
    private static final Component SET_STAND_CARD_MSG = Component.translatable(MOD_ID + ".config.setStandCardMsg");
    private final Screen parent;
    private ComponentList list;
    private final CommonConfig commonConfig = Config.getCommonConfig();
    private final ClientConfig clientConfig = Config.getClientConfig();

    public ConfigScreen(Minecraft ignored, Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        double scroll = this.list == null ? 0.0D : this.list.getScrollAmount();
        this.list = this.addRenderableWidget(new ComponentList(this.minecraft, this.width, this.height - 40, 20, 20));
        this.list.setScrollAmount(scroll);

        this.list.addButton(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(this.commonConfig.standCanOpenDoor.get()).create(0, 0, 0, 20, STAND_CAN_OPEN_DOOR, (cycleButton, aBoolean) -> {
            this.commonConfig.standCanOpenDoor.set(aBoolean);
        }));
        this.list.addButton(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(this.commonConfig.standCanOpenGate.get()).create(0, 0, 0, 20, STAND_CAN_OPEN_GATE, (cycleButton, aBoolean) -> {
            this.commonConfig.standCanOpenGate.set(aBoolean);
        }));
        this.list.addButton(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(this.clientConfig.setStandCardMsg.get()).create(0, 0, 0, 20, SET_STAND_CARD_MSG, (cycleButton, aBoolean) -> {
            this.clientConfig.setStandCardMsg.set(aBoolean);
        }));

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 4, this.height - 20, this.width / 2, 20).build());
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        p_281549_.drawCenteredString(this.font, this.title, this.width / 2, 8, 16777215);
    }

    @Override
    public void removed() {
        Config.save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
