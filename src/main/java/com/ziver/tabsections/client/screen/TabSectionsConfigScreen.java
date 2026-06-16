package com.ziver.tabsections.client.screen;

import com.ziver.tabsections.client.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TabSectionsConfigScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.tabsections.config.title");

    private final Screen parent;

    private Button toggleDebugButton;

    public TabSectionsConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 180;
        int buttonHeight = 20;
        int centerX = this.width / 2 - buttonWidth / 2;

        this.toggleDebugButton = this.addRenderableWidget(Button.builder(getToggleDebugText(), b -> toggleDebugButton()
        ).bounds(centerX, this.height / 2 - 30, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.translatable("screen.tabsections.config.open_debug"),
                b -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new TabSectionsDebugScreen(this));
                    }
                }
        ).bounds(centerX, this.height / 2, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose()
        ).bounds(centerX, this.height / 2 + 40, buttonWidth, buttonHeight).build());
    }

    private void toggleDebugButton() {
        boolean next = !ClientConfig.SHOW_DEBUG_BUTTON.get();

        ClientConfig.SHOW_DEBUG_BUTTON.set(next);
        ClientConfig.SPEC.save();

        this.toggleDebugButton.setMessage(getToggleDebugText());
    }

    private Component getToggleDebugText() {
        return Component.translatable(ClientConfig.SHOW_DEBUG_BUTTON.get() ? "screen.tabsections.config.debug_button.enabled" : "screen.tabsections.config.debug_button.disabled");
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}