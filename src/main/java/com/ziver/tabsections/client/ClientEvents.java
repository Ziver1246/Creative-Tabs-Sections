package com.ziver.tabsections.client;

import com.ziver.tabsections.CreativeTabSections;
import com.ziver.tabsections.tabs.sections.data.TabSectionResourceReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = CreativeTabSections.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(TabSectionResourceReloadListener.INSTANCE);
    }
}