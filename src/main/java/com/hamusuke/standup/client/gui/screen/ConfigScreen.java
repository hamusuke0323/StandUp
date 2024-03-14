package com.hamusuke.standup.client.gui.screen;

import com.hamusuke.standup.config.ClientConfig;
import com.hamusuke.standup.config.CommonConfig;
import com.hamusuke.standup.config.Config;
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
    private final CommonConfig commonConfig = Config.getCommonConfig();
    private final ClientConfig clientConfig = Config.getClientConfig();

    public ConfigScreen(Minecraft ignored, Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(this.commonConfig.standCanOpenDoor.get()).create(this.width / 4, this.height / 2 - 30, this.width / 2, 20, STAND_CAN_OPEN_DOOR, (cycleButton, aBoolean) -> {
            this.commonConfig.standCanOpenDoor.set(aBoolean);
        }));
        this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(this.commonConfig.standCanOpenGate.get()).create(this.width / 4, this.height / 2 - 10, this.width / 2, 20, STAND_CAN_OPEN_GATE, (cycleButton, aBoolean) -> {
            this.commonConfig.standCanOpenGate.set(aBoolean);
        }));
        this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO).withInitialValue(this.clientConfig.setStandCardMsg.get()).create(this.width / 4, this.height / 2 + 10, this.width / 2, 20, SET_STAND_CARD_MSG, (cycleButton, aBoolean) -> {
            this.clientConfig.setStandCardMsg.set(aBoolean);
        }));

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(this.width / 4, this.height - 20, this.width / 2, 20).build());
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
