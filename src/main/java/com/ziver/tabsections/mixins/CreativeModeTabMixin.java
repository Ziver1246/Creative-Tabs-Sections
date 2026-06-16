package com.ziver.tabsections.mixins;

import com.ziver.tabsections.tabs.sections.SectionedTabLayout;
import com.ziver.tabsections.tabs.sections.TabSectionRegistry;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CreativeModeTab.class})
public class CreativeModeTabMixin {

    @Shadow @Mutable private Collection<ItemStack> displayItems;
    @Shadow @Mutable private Set<ItemStack> displayItemsSearchTab;

    @Inject(method = {"buildContents"}, at = {@At("HEAD")}, cancellable = true)
    private void tabsections$buildContents(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        CreativeModeTab self = (CreativeModeTab) (Object) this;

        if (TabSectionRegistry.hasSections(self)) {
            List<ItemStack> display = SectionedTabLayout.buildDisplayItems(self);
            List<ItemStack> search = SectionedTabLayout.buildSearchItems(self);
            this.displayItems = display;
            this.displayItemsSearchTab = new LinkedHashSet<>(search);
            ci.cancel();
        }
    }
}
