package com.ziver.tabsections.mixins;

import com.ziver.tabsections.client.render.SectionRenderState;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CreativeModeInventoryScreen.ItemPickerMenu.class})
public abstract class ItemPickerMenuMixin {
    @Shadow
    protected abstract int getRowIndexForScroll(float var1);

    @Inject(method = {"scrollTo"}, at = {@At("HEAD")})
    private void tabsections$scrollTo(float scroll, CallbackInfo ci) {
        SectionRenderState.currentRow = this.getRowIndexForScroll(scroll);
    }
}
