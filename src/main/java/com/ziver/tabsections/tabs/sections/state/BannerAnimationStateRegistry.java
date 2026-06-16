package com.ziver.tabsections.tabs.sections.state;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class BannerAnimationStateRegistry {

    private static final Map<CreativeModeTab, Map<ResourceLocation, BannerAnimationState>> STATES = new IdentityHashMap<>();

    public static BannerAnimationState get(CreativeModeTab tab, ResourceLocation sectionId) {
        return STATES.computeIfAbsent(tab, ignored -> new HashMap<>()).computeIfAbsent(sectionId, ignored -> new BannerAnimationState());
    }

    public static void clearTab(CreativeModeTab tab) {
        STATES.remove(tab);
    }

    private BannerAnimationStateRegistry() {
    }
}