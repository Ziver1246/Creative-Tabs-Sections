package com.ziver.tabsections.client.render;

import com.ziver.tabsections.tabs.sections.TabSection;
import com.ziver.tabsections.tabs.sections.TabSectionRegistry;
import com.ziver.tabsections.tabs.sections.TabSectionSpriteAnimation;
import com.ziver.tabsections.tabs.sections.state.BannerAnimationState;
import com.ziver.tabsections.tabs.sections.state.BannerAnimationStateRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.CreativeModeTab;

public class SectionHeaderRenderer {
    public static void render(GuiGraphics graphics, int left, int top, CreativeModeTab tab, int mouseX, int mouseY) {
        Font font = Minecraft.getInstance().font;
        int baseX = left + 8;
        int baseY = top + 17;

        for(TabSection section : TabSectionRegistry.orderedSections(tab)) {
            Integer startRow = SectionRenderState.getSectionRow(tab, section.id());
            if (startRow != null) {
                int visibleRow = startRow - SectionRenderState.currentRow;
                if (visibleRow >= 0 && visibleRow <= 4) {
                    int y = baseY + visibleRow * 18;
                    boolean hovering = mouseX >= baseX && mouseX < baseX + 162 && mouseY >= y && mouseY < y + 18;
                    renderSectionHeader(graphics, font, section, tab, baseX, y, hovering);
                }
            }
        }

    }

    private static void renderSectionHeader(GuiGraphics graphics, Font font, TabSection section, CreativeModeTab tab, int x, int y, boolean hovering) {
        graphics.blit(SectionHeaderStyle.FRAME_TEXTURE, x, y, 0.0F, 0.0F, 162, 18, 162, 18);
        drawBanner(graphics, section, tab, x, y, hovering);
        int textWidth = font.width(section.title());
        int labelX1 = x + 2;
        int labelY1 = y + 2;
        int labelX2 = labelX1 + 8 + textWidth;
        int labelY2 = y + 18 - 2;
        graphics.fill(labelX1, labelY1, labelX2, labelY2, section.textBackgroundColor());
        graphics.drawString(font, section.title(), x + 5, y + 5, section.textColor(), section.textShadow());
    }

    private static void drawBanner(GuiGraphics graphics, TabSection section, CreativeModeTab tab, int x, int y, boolean hovering) {
        TabSectionSpriteAnimation animation = section.visuals().spriteAnimation();
        if (animation != null && animation.frames() > 1) {
            BannerAnimationState state = BannerAnimationStateRegistry.get(tab, section.id());
            long gameTime = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L;
            if (animation.animateOnHover()) {
                state.setPaused(!hovering, gameTime);
            }

            int frame = state.getFrame(gameTime, animation.frames(), animation.frameTime());
            int u = 0;
            int v = frame * 18;
            graphics.blit(section.bannerTexture(), x, y, (float)u, (float)v, 162, 18, 162, 18 * animation.frames());
        } else {
            graphics.blit(section.bannerTexture(), x, y, 0.0F, 0.0F, 162, 18, 162, 18);
        }
    }

    private SectionHeaderRenderer() {}
}
