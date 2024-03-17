package com.hamusuke.standup.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ComponentList extends ContainerObjectSelectionList<ComponentList.Entry> {
    public ComponentList(Minecraft mc, int width, int height, int y, int itemHeight) {
        super(mc, width, height, y, itemHeight);
    }

    public void addString(Component component) {
        this.addEntry(new StringEntry(component));
    }

    public void addButton(AbstractButton button) {
        this.addEntry(new ButtonEntry(button));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTicks) {
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class StringEntry extends Entry {
        final Component name;
        private final int width;

        public StringEntry(Component p_193886_) {
            this.name = p_193886_;
            this.width = ComponentList.this.minecraft.font.width(this.name);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTicks) {
            guiGraphics.drawString(ComponentList.this.minecraft.font, this.name, ComponentList.this.minecraft.screen.width / 2 - this.width / 2, top + height - 9 - 1, 16777215, false);
        }

        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent p_265391_) {
            return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class ButtonEntry extends Entry {
        final AbstractButton button;

        public ButtonEntry(AbstractButton button) {
            this.button = button;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTicks) {
            this.button.setX(left);
            this.button.setY(top);
            this.button.setWidth(width);
            this.button.render(guiGraphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.button);
        }
    }
}
