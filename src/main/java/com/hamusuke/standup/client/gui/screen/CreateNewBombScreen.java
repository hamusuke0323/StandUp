package com.hamusuke.standup.client.gui.screen;

import com.hamusuke.standup.client.StandUpClient;
import com.hamusuke.standup.client.gui.component.ComponentList;
import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.InteractionDataSerializer;
import com.hamusuke.standup.network.packet.c2s.AskBombInfoRsp;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.What;
import com.hamusuke.standup.stand.ability.deadly_queen.bomb.Bomb.When;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hamusuke.standup.StandUp.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class CreateNewBombScreen extends Screen {
    private static final Component TITLE = Component.translatable(MOD_ID + ".screen.bomb");
    private static final Component WHEN = Component.translatable(MOD_ID + ".screen.bomb.when");
    private static final Component WHAT = Component.translatable(MOD_ID + ".screen.bomb.what");
    private final HitResult result;
    private final Component targetDesc;
    private ComponentList list;
    private When when = StandUpClient.whenRef;
    private What what = StandUpClient.whatRef;

    public CreateNewBombScreen(HitResult result) {
        super(TITLE);

        this.result = result;
        this.targetDesc = createTargetDescription(result);
    }

    private static Component createTargetDescription(HitResult result) {
        if (result instanceof BlockHitResult blockHitResult) {
            return Component.literal(blockHitResult.getBlockPos().toString());
        } else if (result instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity().getDisplayName();
        }

        return Component.empty();
    }

    @Override
    protected void init() {
        double scroll = this.list == null ? 0.0D : this.list.getScrollAmount();
        this.list = this.addRenderableWidget(new ComponentList(this.minecraft, this.width, this.height - 40 - 20, 40, 20));
        this.list.setScrollAmount(scroll);

        this.list.addString(WHEN);
        this.list.addButton(CycleButton.builder(When::getDesc).withValues(When.values()).withInitialValue(this.when).displayOnlyValue().create(0, 0, 0, 20, Component.empty(), (cycleButton, when1) -> this.when = when1));
        this.list.addString(WHAT);
        this.list.addButton(CycleButton.builder(What::getDesc).withValues(What.values()).withInitialValue(this.what).displayOnlyValue().create(0, 0, 0, 20, Component.empty(), (cycleButton, what1) -> this.what = what1));

        this.addRenderableWidget(Button
                .builder(CommonComponents.GUI_CANCEL, button -> this.onClose())
                .bounds(0, this.height - 20, this.width / 2, 20)
                .build());
        this.addRenderableWidget(Button
                .builder(CommonComponents.GUI_DONE, button -> this.done())
                .bounds(this.width / 2, this.height - 20, this.width / 2, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);

        p_281549_.drawCenteredString(this.font, this.getTitle(), this.width / 2, 8, 16777215);
        p_281549_.drawCenteredString(this.font, this.targetDesc, this.width / 2, 26, 16777215);
    }

    @Override
    public void renderBackground(GuiGraphics p_283688_, int p_299421_, int p_298679_, float p_297268_) {
        this.renderDirtBackground(p_283688_);
    }

    private void done() {
        this.onClose();

        NetworkManager.sendToServer(new AskBombInfoRsp(new InteractionDataSerializer(this.result), this.when, this.what));
        StandUpClient.whenRef = this.when;
        StandUpClient.whatRef = this.what;
    }
}
