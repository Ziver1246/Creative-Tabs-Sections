package com.ziver.tabsections.tabs.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.ziver.tabsections.tabs.sections.TabSectionVisuals;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class TabSection {
    private final ResourceLocation parentTabId;
    private final ResourceLocation id;
    private final int priority;
    private final List<Supplier<ItemStack>> items = new ArrayList();
    private TabSectionVisuals visuals;

    public TabSection(ResourceLocation parentTabId, ResourceLocation id, int priority) {
        this.visuals = TabSectionVisuals.FALLBACK;
        this.parentTabId = parentTabId;
        this.id = id;
        this.priority = priority;
    }

    public ResourceLocation parentTabId() {
        return this.parentTabId;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public int priority() {
        return this.priority;
    }

    public List<Supplier<ItemStack>> items() {
        return this.items;
    }

    public void setVisuals(TabSectionVisuals visuals) {
        if (visuals != null) {
            this.visuals = visuals;
        }

    }

    public TabSectionVisuals visuals() {
        return this.visuals;
    }

    public Component title() {
        return this.visuals.title();
    }

    public ResourceLocation bannerTexture() {
        return this.visuals.bannerTexture();
    }

    public int textColor() {
        return this.visuals.textColor();
    }

    public int textBackgroundColor() {
        return this.visuals.textBackgroundColor();
    }

    public boolean textShadow() {
        return this.visuals.textShadow();
    }

    public void addItem(Supplier<ItemStack> supplier) {
        if (supplier != null) {
            this.items.add(supplier);
        }

    }

    @SafeVarargs
    public final void add(Supplier<? extends ItemLike>... itemSuppliers) {
        if (itemSuppliers == null) return;

        for (Supplier<? extends ItemLike> supplier : itemSuppliers) {
            if (supplier == null) continue;
            items.add(() -> new ItemStack(supplier.get()));
        }
    }

    public void addStack(Supplier<ItemStack> supplier) {
        if (supplier != null) {
            this.items.add(supplier);
        }

    }
}
