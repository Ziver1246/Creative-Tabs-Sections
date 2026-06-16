package com.ziver.tabsections.client.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class SectionRenderState {

    public static int currentRow = 0;

    private static final Map<CreativeModeTab, Map<ResourceLocation, Integer>> SECTION_START_ROWS = new IdentityHashMap<>();

    public static void clear(CreativeModeTab tab) {
        SECTION_START_ROWS.remove(tab);
    }

    public static void setSectionRow(CreativeModeTab tab, ResourceLocation sectionId, int row) {
        SECTION_START_ROWS.computeIfAbsent(tab, ignored -> new HashMap<>()).put(sectionId, row);
    }

    public static Integer getSectionRow(CreativeModeTab tab, ResourceLocation sectionId) {
        Map<ResourceLocation, Integer> rows = SECTION_START_ROWS.get(tab);
        if (rows == null) {
            return null;
        }
        return rows.get(sectionId);
    }

    private SectionRenderState() {}
}