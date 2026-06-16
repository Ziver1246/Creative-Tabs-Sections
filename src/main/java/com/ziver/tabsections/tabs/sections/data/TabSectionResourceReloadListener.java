package com.ziver.tabsections.tabs.sections.data;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class TabSectionResourceReloadListener implements ResourceManagerReloadListener {

    private TabSectionResourceReloadListener() {}

    public static final TabSectionResourceReloadListener INSTANCE = new TabSectionResourceReloadListener();

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        TabSectionJsonLoader.loadAll(resourceManager);
    }
}