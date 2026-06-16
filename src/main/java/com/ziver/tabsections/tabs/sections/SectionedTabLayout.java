package com.ziver.tabsections.tabs.sections;

import com.ziver.tabsections.client.render.SectionRenderState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class SectionedTabLayout {
    public static List<ItemStack> buildDisplayItems(CreativeModeTab tab) {
        List<ItemStack> result = new ArrayList<>();
        SectionRenderState.clear(tab);

        for(int i = 0; i < 9; ++i) {
            result.add(ItemStack.EMPTY);
        }

        int y = 0;
        List<TabSection> sections = TabSectionRegistry.orderedSections(tab);

        for(int s = 0; s < sections.size(); ++s) {
            TabSection section = sections.get(s);
            SectionRenderState.setSectionRow(tab, section.id(), y);
            int itemCount = 0;

            for(Supplier<ItemStack> supplier : section.items()) {
                ItemStack stack = supplier.get();
                if (!stack.isEmpty()) {
                    result.add(stack);
                    ++itemCount;
                }
            }

            int rowCount = (int)Math.ceil((double)itemCount / (double)9.0F);
            y += rowCount + 1;
            boolean isLast = s == sections.size() - 1;
            if (isLast) {
                break;
            }

            int padding = 9 - itemCount % 9;
            if (padding < 9) {
                padding += 9;
            }

            for(int i = 0; i < padding; ++i) {
                result.add(ItemStack.EMPTY);
            }
        }

        return result;
    }

    public static List<ItemStack> buildSearchItems(CreativeModeTab tab) {
        List<ItemStack> result = new ArrayList<>();

        for(TabSection section : TabSectionRegistry.orderedSections(tab)) {
            for(Supplier<ItemStack> supplier : section.items()) {
                ItemStack stack = supplier.get();
                if (!stack.isEmpty()) {
                    result.add(stack);
                }
            }
        }

        return result;
    }

    private SectionedTabLayout() {}
}
