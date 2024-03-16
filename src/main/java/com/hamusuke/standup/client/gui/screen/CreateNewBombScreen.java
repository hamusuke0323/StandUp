package com.hamusuke.standup.client.gui.screen;

import com.hamusuke.standup.network.NetworkManager;
import com.hamusuke.standup.network.packet.InteractionDataSerializer;
import com.hamusuke.standup.network.packet.c2s.DeadlyQueenWantsToKnowNewBombInfoRsp;
import com.hamusuke.standup.stand.ability.deadly_queen.Bomb.What;
import com.hamusuke.standup.stand.ability.deadly_queen.Bomb.When;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
    private final HitResult result;
    private final Component targetDesc;
    private When when = When.PUSH_SWITCH;
    private What what = What.SELF;

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

        p_281549_.drawCenteredString(this.font, this.getTitle(), this.width / 2, 20, 16777215);
        p_281549_.drawCenteredString(this.font, this.targetDesc, this.width / 2, 40, 16777215);
    }

    private void done() {
        this.onClose();

        NetworkManager.sendToServer(new DeadlyQueenWantsToKnowNewBombInfoRsp(new InteractionDataSerializer(this.result), this.when, this.what));
    }
}
