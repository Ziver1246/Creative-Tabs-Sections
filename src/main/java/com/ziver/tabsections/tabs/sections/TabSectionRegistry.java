package com.ziver.tabsections.tabs.sections;

import com.ziver.tabsections.CreativeTabSections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class TabSectionRegistry {
    private static final Map<ResourceLocation, TabSection> SECTIONS = new LinkedHashMap();

    public static TabSection register(ResourceLocation parentTabId, ResourceLocation id, int priority) {
        TabSection existing = (TabSection)SECTIONS.get(id);
        if (existing != null) {
            CreativeTabSections.LOGGER.warn("Duplicate tab section registration ignored: {}", id);
            return existing;
        } else {
            TabSection section = new TabSection(parentTabId, id, priority);
            SECTIONS.put(id, section);
            return section;
        }
    }

    public static TabSection get(ResourceLocation id) {
        return (TabSection)SECTIONS.get(id);
    }

    public static Iterable<TabSection> allSections() {
        return SECTIONS.values();
    }

    public static boolean hasSections(CreativeModeTab tab) {
        ResourceLocation tabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        return tabId == null ? false : SECTIONS.values().stream().anyMatch((section) -> section.parentTabId().equals(tabId));
    }

    public static List<TabSection> orderedSections(CreativeModeTab tab) {
        ResourceLocation tabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
        return tabId == null ? List.of() : SECTIONS.values().stream().filter((section) -> section.parentTabId().equals(tabId)).sorted(Comparator.comparingInt(TabSection::priority)).toList();
    }

    private TabSectionRegistry() {}
}
