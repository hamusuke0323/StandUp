package com.hamusuke.standup.client.gui.screen;

import com.hamusuke.standup.CommonConfig;
import net.minecraft.client.Minecraft;
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

    public ConfigScreen(Minecraft ignored, Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(CommonConfig.standCanOpenDoor).create(this.width / 4, this.height / 2 - 30, this.width / 2, 20, STAND_CAN_OPEN_DOOR, (cycleButton, aBoolean) -> {
            CommonConfig.standCanOpenDoor = aBoolean;
        }));
        this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(CommonConfig.standCanOpenGate).create(this.width / 4, this.height / 2 - 10, this.width / 2, 20, STAND_CAN_OPEN_GATE, (cycleButton, aBoolean) -> {
            CommonConfig.standCanOpenGate = aBoolean;
        }));
        this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(CommonConfig.setStandCardMsg).create(this.width / 4, this.height / 2 + 10, this.width / 2, 20, SET_STAND_CARD_MSG, (cycleButton, aBoolean) -> {
            CommonConfig.setStandCardMsg = aBoolean;
        }));

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 4, this.height - 20, this.width / 2, 20).build());
    }

    @Override
    public void removed() {
        CommonConfig.save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
