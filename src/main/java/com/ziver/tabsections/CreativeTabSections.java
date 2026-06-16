package com.ziver.tabsections;

import com.ziver.tabsections.client.ClientConfig;
import com.ziver.tabsections.client.screen.TabSectionsConfigScreen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreativeTabSections.MOD_ID)
public class CreativeTabSections {

    public static final String MOD_ID = "tabs_sections";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public CreativeTabSections(IEventBus modEventBus, ModContainer modContainer) {

        LOGGER.info("Initializing {}", MOD_ID);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> new TabSectionsConfigScreen(parent));
        }

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
