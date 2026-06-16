package com.ziver.tabsections.tabs.sections.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ziver.tabsections.CreativeTabSections;
import com.ziver.tabsections.tabs.sections.TabSection;
import com.ziver.tabsections.tabs.sections.TabSectionRegistry;
import com.ziver.tabsections.tabs.sections.TabSectionSpriteAnimation;
import com.ziver.tabsections.tabs.sections.TabSectionVisuals;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TabSectionJsonLoader {

    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = CreativeTabSections.LOGGER;

    public static void loadAll(ResourceManager resourceManager) {
        for (TabSection section : TabSectionRegistry.allSections()) {
            loadSection(resourceManager, section);
        }
    }

    private static void loadSection(ResourceManager resourceManager, TabSection section) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(section.id().getNamespace(), "tabsections/" + section.id().getPath() + ".json");

        try {
            Optional<Resource> resourceOpt = resourceManager.getResource(location);

            if (resourceOpt.isEmpty()) {
                LOGGER.warn("Missing section config for '{}'. Using fallback visuals.", section.id());
                return;
            }

            try (InputStream stream = resourceOpt.get().open();
                 InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                TabSectionJsonData data = GSON.fromJson(reader, TabSectionJsonData.class);

                if (data == null) {
                    LOGGER.warn("Empty section config for '{}'. Using fallback visuals.", section.id());
                    return;
                }

                TabSectionVisuals visuals = new TabSectionVisuals(
                        buildTitle(section, data),
                        buildBanner(section, data),
                        parseColorOrDefault(data.text_color, TabSectionVisuals.FALLBACK.textColor()),
                        parseColorOrDefault(data.label_color, TabSectionVisuals.FALLBACK.textBackgroundColor()),
                        data.shadow != null ? data.shadow : TabSectionVisuals.FALLBACK.textShadow(),
                        buildAnimation(data)
                );

                section.setVisuals(visuals);
            }

        } catch (JsonSyntaxException e) {
            LOGGER.error("Invalid JSON in section config '{}'. Using fallback visuals.", section.id(), e);
        } catch (Exception e) {
            LOGGER.error("Failed loading section config '{}'. Using fallback visuals.", section.id(), e);
        }
    }

    private static Component buildTitle(TabSection section, TabSectionJsonData data) {
        if (data.title_key == null || data.title_key.isBlank()) {
            LOGGER.warn("Section '{}' is missing 'title_key'. Using id as fallback title.", section.id());
            return Component.literal(section.id().getPath());
        }

        return Component.translatable(data.title_key);
    }

    private static ResourceLocation buildBanner(TabSection section, TabSectionJsonData data) {
        if (data.banner == null || data.banner.isBlank()) {
            LOGGER.warn("Section '{}' is missing 'banner'. Using missing texture.", section.id());
            return MissingTextureAtlasSprite.getLocation();
        }

        try {
            if (data.banner.contains(":")) {
                return ResourceLocation.parse(data.banner);
            }

            return ResourceLocation.fromNamespaceAndPath(section.id().getNamespace(), data.banner);
        } catch (Exception e) {
            LOGGER.warn("Invalid banner location '{}' for section '{}'. Using missing texture.", data.banner, section.id(), e);
            return MissingTextureAtlasSprite.getLocation();
        }
    }

    private static TabSectionSpriteAnimation buildAnimation(TabSectionJsonData data) {
        if (data.sprite_animation == null) {
            return null;
        }

        int frameTime = data.sprite_animation.frame_time != null ? data.sprite_animation.frame_time : 1;
        int frames = data.sprite_animation.frames != null ? data.sprite_animation.frames : 1;
        boolean animateOnHover = data.sprite_animation.animate_on_hover != null && data.sprite_animation.animate_on_hover;

        return new TabSectionSpriteAnimation(frameTime, frames, animateOnHover);
    }

    private static int parseColorOrDefault(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        try {
            String hex = value.startsWith("#") ? value.substring(1) : value;
            return (int) Long.parseLong(hex, 16);
        } catch (Exception e) {
            LOGGER.warn("Invalid color '{}'. Using fallback.", value);
            return fallback;
        }
    }

    private TabSectionJsonLoader() {
    }
}