package com.ziver.tabsections.tabs.sections;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabSectionVisuals {
    private final Component title;
    private final ResourceLocation bannerTexture;
    private final int textColor;
    private final int textBackgroundColor;
    private final boolean textShadow;
    private final TabSectionSpriteAnimation spriteAnimation;

    public static final TabSectionVisuals FALLBACK = new TabSectionVisuals(Component.literal("Missing banner config"), MissingTextureAtlasSprite.getLocation(), 0xFFFFFFFF, 0xAA550000, false, null);

    public TabSectionVisuals(Component title, ResourceLocation bannerTexture, int textColor, int textBackgroundColor, boolean textShadow, TabSectionSpriteAnimation spriteAnimation) {
        this.title = title;
        this.bannerTexture = bannerTexture;
        this.textColor = textColor;
        this.textBackgroundColor = textBackgroundColor;
        this.textShadow = textShadow;
        this.spriteAnimation = spriteAnimation;
    }

    public Component title() {
        return title;
    }

    public ResourceLocation bannerTexture() {
        return bannerTexture;
    }

    public int textColor() {
        return textColor;
    }

    public int textBackgroundColor() {
        return textBackgroundColor;
    }

    public boolean textShadow() {
        return textShadow;
    }

    public TabSectionSpriteAnimation spriteAnimation() {
        return spriteAnimation;
    }

    public boolean isAnimated() {
        return spriteAnimation != null;
    }
}