package com.ziver.tabsections.mixins;

import com.ziver.tabsections.client.ClientConfig;
import com.ziver.tabsections.client.screen.TabSectionsDebugScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void tabsections$addDebugButton(CallbackInfo ci) {
        if (!ClientConfig.SHOW_DEBUG_BUTTON.get()) return;

        Component text = Component.translatable("screen.tabsections.debug.pause_button");

        int textWidth = this.font.width(text);
        int padding = 20;
        int buttonWidth = textWidth + padding;

        int offsetX = 112;
        int x = this.width / 2 + offsetX;

        int offsetY = 51;
        int y = this.height / 4 + offsetY;

        this.addRenderableWidget(Button.builder(text, b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new TabSectionsDebugScreen(this));
            }
        }).bounds(x, y, buttonWidth, 20).build());
    }
}