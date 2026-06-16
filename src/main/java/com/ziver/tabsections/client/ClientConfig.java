package com.ziver.tabsections.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue SHOW_DEBUG_BUTTON;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.push("debug");

        SHOW_DEBUG_BUTTON = b
                .translation("tabsections.config.show_debug_button")
                .comment("Show the Tab Sections debug button in the pause menu")
                .define("show_debug_button", false);

        b.pop();

        SPEC = b.build();
    }

    private ClientConfig() {}
}