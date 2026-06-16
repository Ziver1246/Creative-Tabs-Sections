package com.ziver.tabsections.mixins;

import com.ziver.tabsections.client.render.SectionHeaderRenderer;
import com.ziver.tabsections.tabs.sections.TabSectionRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CreativeModeInventoryScreen.class})
public class CreativeModeInventoryScreenMixin {

    @Shadow @Final private static CreativeModeTab selectedTab;

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void tabsections$render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (TabSectionRegistry.hasSections(selectedTab)) {

            CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;
            AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor)screen;
            SectionHeaderRenderer.render(graphics, accessor.tabsections$getLeftPos(), accessor.tabsections$getTopPos(), selectedTab, mouseX, mouseY);
        }
    }
}
