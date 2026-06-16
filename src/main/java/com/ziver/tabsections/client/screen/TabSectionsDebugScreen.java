package com.ziver.tabsections.client.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ziver.tabsections.client.render.SectionHeaderStyle;
import com.ziver.tabsections.tabs.sections.TabSection;
import com.ziver.tabsections.tabs.sections.TabSectionRegistry;
import com.ziver.tabsections.tabs.sections.TabSectionSpriteAnimation;
import com.ziver.tabsections.tabs.sections.state.BannerAnimationState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TabSectionsDebugScreen extends Screen {

    private static final Component TITLE = Component.translatable("screen.tabsections.debug.title");
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    // Layout
    private static final int OUTER_MARGIN = 12;
    private static final int TOP_START = 30;
    private static final int BOTTOM_RESERVED = 34;

    private static final int BLOCK_PADDING = 6;
    private static final int BLOCK_SPACING = 10;

    private static final int LINE_HEIGHT = 10;
    private static final int SECTION_HEADER_HEIGHT = 14;

    // Banner layout
    // Increase/decrease these values to move the banner or add more spacing around it.
    private static final int BANNER_W = 162;
    private static final int BANNER_H = 18;

    // Space between the metadata block and the Banner label.
    private static final int BANNER_SECTION_TOP_GAP = 18;

    // Space between "Banner:" and the rendered banner.
    private static final int BANNER_LABEL_TO_RENDER_GAP = 2;

    // Move the banner horizontally relative to the content left.
    // 0 = aligned with text. Increase to move right.
    private static final int BANNER_OFFSET_X = 0;

    private static final int BANNER_BOTTOM_GAP = 16;
    private static final int DUAL_FIELD_GAP = 18;

    // JSON / error box
    private static final int JSON_BOX_TEXT_PADDING = 4;
    private static final int JSON_BOX_VERTICAL_PADDING = 3;
    private static final int JSON_BOX_EXTRA_HEIGHT = 6;

    private static final int COPY_BUTTON_WIDTH = 42;
    private static final int COPY_BUTTON_HEIGHT = 14;
    private static final int COPY_BUTTON_MARGIN = 4;

    // ===== Colors - General =====
    private static final int COLOR_SCREEN_TITLE = 0xFFFFFF;
    private static final int COLOR_META_TOP = 0x909090;

    private static final int COLOR_PANEL_BACKGROUND = 0x66000000;
    private static final int COLOR_PANEL_BORDER = 0x44FFFFFF;


    // ===== Colors - Field Labels (uniform, low emphasis) =====
    private static final int COLOR_LABEL_MOD = 0xAAAAAA;
    private static final int COLOR_LABEL_MOD_ID = 0xAAAAAA;
    private static final int COLOR_LABEL_PARENT_TAB = 0xAAAAAA;
    private static final int COLOR_LABEL_TAB_ID = 0xAAAAAA;
    private static final int COLOR_LABEL_SECTION_ID = 0xAAAAAA;
    private static final int COLOR_LABEL_PRIORITY = 0xAAAAAA;
    private static final int COLOR_LABEL_ANIMATED = 0xAAAAAA;
    private static final int COLOR_LABEL_BANNER = 0xAAAAAA;
    private static final int COLOR_LABEL_SOURCE_JSON = 0xAAAAAA;


    // ===== Colors - Field Values (higher emphasis) =====
    private static final int COLOR_VALUE_MOD = 0xFFCC66;
    private static final int COLOR_VALUE_MOD_ID = 0xFFCC66;

    private static final int COLOR_VALUE_PARENT_TAB = 0xFFFFFF;
    private static final int COLOR_VALUE_TAB_ID = 0xFFFFFF;

    private static final int COLOR_VALUE_SECTION_ID = 0xFFFFFF;
    private static final int COLOR_VALUE_PRIORITY = 0xFFFFFF;

    private static final int COLOR_ANIMATED_SEPARATOR = 0xAAAAAA;
    private static final int COLOR_ANIMATED_FIELD = 0xFFCC66;
    private static final int COLOR_ANIMATED_VALUE = 0xFFFFFF;


    // ===== Colors - JSON Box =====
    private static final int COLOR_JSON_BOX_BACKGROUND = 0x44000000;
    private static final int COLOR_JSON_BOX_BORDER = 0x33FFFFFF;


    // ===== Colors - Error Box =====
    private static final int COLOR_ERROR_BOX_BACKGROUND = 0x33100000;
    private static final int COLOR_ERROR_BOX_BORDER = 0xAAFF5555;
    private static final int COLOR_ERROR_TEXT = 0xFF6666;


    // ===== Colors - JSON Syntax Highlight =====
    private static final int COLOR_JSON_DEFAULT = 0xA9B7C6;
    private static final int COLOR_JSON_BRACES = 0xFFFFFF;
    private static final int COLOR_JSON_PUNCTUATION = 0xFFFFFF;
    private static final int COLOR_JSON_KEY = 0xCC66CC;
    private static final int COLOR_JSON_STRING = 0x90E090;
    private static final int COLOR_JSON_NUMBER = 0xA0D0FF;
    private static final int COLOR_JSON_BOOLEAN = 0xFFAA33;
    private static final int COLOR_JSON_NULL = 0xFFAA33;

    // ===== Colors - Copy Button =====
    private static final int COLOR_COPY_BUTTON_BACKGROUND = 0x55222222;
    private static final int COLOR_COPY_BUTTON_HOVER_BACKGROUND = 0x77444444;
    private static final int COLOR_COPY_BUTTON_BORDER = 0x55FFFFFF;
    private static final int COLOR_COPY_BUTTON_TEXT = 0xFFFFFF;

    private final Screen parent;
    private final List<TabSection> sections = new ArrayList<>();
    private final Map<ResourceLocation, BannerAnimationState> previewAnimationStates = new HashMap<>();
    private final List<CopyJsonHitbox> copyJsonHitboxes = new ArrayList<>();

    private Button backButton;

    private int scrollOffset = 0;
    private int contentHeight = 0;

    private long animationStartMillis;
    private long lastRenderMillis;

    public TabSectionsDebugScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.sections.clear();
        this.sections.addAll((Collection<? extends TabSection>) TabSectionRegistry.allSections());

        this.sections.sort(Comparator.comparing((TabSection s) -> s.id().getNamespace())
                .thenComparing(s -> resolveModDisplayName(s.id().getNamespace()).toLowerCase())
                .thenComparing(s -> s.id().getPath())
        );

        this.backButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), b -> onClose())
                .bounds(OUTER_MARGIN, 6, 60, 20).build());

        this.animationStartMillis = System.currentTimeMillis();
        this.lastRenderMillis = this.animationStartMillis;

        clampScroll();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scrollOffset -= (int) (scrollY * 18.0D);
        clampScroll();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (CopyJsonHitbox hitbox : this.copyJsonHitboxes) {
                if (hitbox.contains(mouseX, mouseY)) {
                    if (this.minecraft != null && hitbox.json() != null) {
                        this.minecraft.keyboardHandler.setClipboard(hitbox.json());
                    }
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.scrollOffset -= 18;
            clampScroll();
            return true;
        }

        if (keyCode == 264) {
            this.scrollOffset += 18;
            clampScroll();
            return true;
        }

        if (keyCode == 266) {
            this.scrollOffset -= getVisibleHeight();
            clampScroll();
            return true;
        }

        if (keyCode == 267) {
            this.scrollOffset += getVisibleHeight();
            clampScroll();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int headerY = 6 - this.scrollOffset;
        this.backButton.setX(OUTER_MARGIN);
        this.backButton.setY(headerY);
        this.backButton.visible = headerY + this.backButton.getHeight() > 0 && headerY < this.height;

        super.render(graphics, mouseX, mouseY, partialTick);

        this.lastRenderMillis = System.currentTimeMillis();
        this.copyJsonHitboxes.clear();

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12 - this.scrollOffset, COLOR_SCREEN_TITLE);

        int x = OUTER_MARGIN;
        int y = TOP_START - this.scrollOffset;
        int width = this.width - (OUTER_MARGIN * 2);

        graphics.drawString(this.font, Component.translatable("screen.tabsections.debug.registered_sections", this.sections.size()), x, y, COLOR_META_TOP);
        y += SECTION_HEADER_HEIGHT;

        int visibleBottom = this.height - BOTTOM_RESERVED;

        for (TabSection section : this.sections) {
            SectionRenderData data = buildRenderData(section);
            int blockHeight = computeBlockHeight(data);

            if (y + blockHeight >= TOP_START && y <= visibleBottom) {
                renderSectionBlock(graphics, section, data, x, y, width, mouseX, mouseY, blockHeight);
            }

            y += blockHeight + BLOCK_SPACING;
        }

        this.contentHeight = Math.max(0, y - (TOP_START - this.scrollOffset));
        clampScroll();
    }

    private void renderSectionBlock(GuiGraphics graphics, TabSection section, SectionRenderData data, int x, int y, int width, int mouseX, int mouseY, int blockHeight) {
        int x2 = x + width;
        int y2 = y + blockHeight;

        drawPanel(graphics, x, y, x2, y2);

        int tx = x + BLOCK_PADDING;
        int ty = y + BLOCK_PADDING;
        int contentWidth = x2 - tx - BLOCK_PADDING;

        renderDualFieldLine(graphics, Component.translatable("screen.tabsections.debug.mod_name_label"), data.modDisplayName(),
                Component.translatable("screen.tabsections.debug.mod_id_label"), data.modId(), tx, ty, contentWidth,
                COLOR_LABEL_MOD, COLOR_VALUE_MOD, COLOR_LABEL_MOD_ID, COLOR_VALUE_MOD_ID
        );
        ty += 12;

        renderDualFieldLine(graphics, Component.translatable("screen.tabsections.debug.parent_tab_label"), data.parentDisplayName(),
                Component.translatable("screen.tabsections.debug.tab_id_label"), data.parentTabId(), tx, ty, contentWidth,
                COLOR_LABEL_PARENT_TAB, COLOR_VALUE_PARENT_TAB, COLOR_LABEL_TAB_ID, COLOR_VALUE_TAB_ID
        );
        ty += LINE_HEIGHT;

        renderFieldLine(graphics, Component.translatable("screen.tabsections.debug.section_id_label"), data.sectionDisplayId(), tx, ty, COLOR_LABEL_SECTION_ID, COLOR_VALUE_SECTION_ID);
        ty += LINE_HEIGHT;

        renderFieldLine(graphics, Component.translatable("screen.tabsections.debug.priority_label"), String.valueOf(section.priority()), tx, ty, COLOR_LABEL_PRIORITY, COLOR_VALUE_PRIORITY);
        ty += LINE_HEIGHT;

        renderAnimatedLine(graphics, section, tx, ty);
        ty += BANNER_SECTION_TOP_GAP;

        graphics.drawString(this.font, Component.translatable("screen.tabsections.debug.banner_label"), tx, ty, COLOR_LABEL_BANNER);
        ty += LINE_HEIGHT + BANNER_LABEL_TO_RENDER_GAP;

        int previewX = tx + BANNER_OFFSET_X;
        int previewY = ty;

        boolean hoveringPreview = mouseX >= previewX && mouseX < previewX + BANNER_W && mouseY >= previewY && mouseY < previewY + BANNER_H;

        renderBannerPreview(graphics, section, previewX, previewY, hoveringPreview);
        ty += BANNER_H + BANNER_BOTTOM_GAP;

        graphics.drawString(this.font, Component.translatable("screen.tabsections.debug.source_json_label"), tx, ty, COLOR_LABEL_SOURCE_JSON);
        ty += 12;

        if (data.missingJsonError() != null) {
            renderErrorBox(graphics, data.missingJsonError(), tx, ty, x2 - BLOCK_PADDING);
        } else {
            renderJsonBox(graphics, section.id(), data.jsonLines(), data.rawJson(), tx, ty, x2 - BLOCK_PADDING, mouseX, mouseY);
        }
    }

    private void renderFieldLine(GuiGraphics graphics, Component label, String value, int x, int y, int labelColor, int valueColor) {
        String labelText = label.getString();
        graphics.drawString(this.font, labelText, x, y, labelColor);
        graphics.drawString(this.font, value, x + this.font.width(labelText), y, valueColor);
    }

    private void renderAnimatedLine(GuiGraphics graphics, TabSection section, int x, int y) {
        String label = Component.translatable("screen.tabsections.debug.animated_label").getString();

        int drawX = x;

        graphics.drawString(this.font, label, drawX, y, COLOR_LABEL_ANIMATED);
        drawX += this.font.width(label);

        TabSectionSpriteAnimation animation = section.visuals().spriteAnimation();

        if (animation == null) {
            String no = Component.translatable("screen.tabsections.debug.animation.no").getString();
            graphics.drawString(this.font, no, drawX, y, COLOR_ANIMATED_VALUE);
            return;
        }

        String yes = Component.translatable("screen.tabsections.debug.value.yes").getString();
        String no = Component.translatable("screen.tabsections.debug.value.no").getString();

        drawX = drawAnimatedPart(graphics, yes, drawX, y, COLOR_ANIMATED_VALUE);

        drawX = drawAnimatedPart(graphics, " | ", drawX, y, COLOR_ANIMATED_SEPARATOR);
        drawX = drawAnimatedPart(graphics, Component.translatable("screen.tabsections.debug.frames_label").getString(), drawX, y, COLOR_ANIMATED_FIELD);
        drawX = drawAnimatedPart(graphics, String.valueOf(animation.frames()), drawX, y, COLOR_ANIMATED_VALUE);

        drawX = drawAnimatedPart(graphics, " | ", drawX, y, COLOR_ANIMATED_SEPARATOR);
        drawX = drawAnimatedPart(graphics, Component.translatable("screen.tabsections.debug.frame_time_label").getString(), drawX, y, COLOR_ANIMATED_FIELD);
        drawX = drawAnimatedPart(graphics, String.valueOf(animation.frameTime()), drawX, y, COLOR_ANIMATED_VALUE);

        drawX = drawAnimatedPart(graphics, " | ", drawX, y, COLOR_ANIMATED_SEPARATOR);
        drawX = drawAnimatedPart(graphics, Component.translatable("screen.tabsections.debug.hover_trigger_label").getString(), drawX, y, COLOR_ANIMATED_FIELD);
        drawAnimatedPart(graphics, animation.animateOnHover() ? yes : no, drawX, y, COLOR_ANIMATED_VALUE);
    }

    private int drawAnimatedPart(GuiGraphics graphics, String text, int x, int y, int color) {
        graphics.drawString(this.font, text, x, y, color);
        return x + this.font.width(text);
    }

    private void renderDualFieldLine(GuiGraphics graphics, Component leftLabel, String leftValue, Component rightLabel, String rightValue, int x, int y, int totalWidth, int leftLabelColor, int leftValueColor, int rightLabelColor, int rightValueColor) {
        String leftLabelText = leftLabel.getString();
        String rightLabelText = rightLabel.getString();

        int leftLabelWidth = this.font.width(leftLabelText);
        int leftValueWidth = this.font.width(leftValue);

        int rightX = x + leftLabelWidth + leftValueWidth + DUAL_FIELD_GAP;

        graphics.drawString(this.font, leftLabelText, x, y, leftLabelColor);
        graphics.drawString(this.font, leftValue, x + leftLabelWidth, y, leftValueColor);

        if (rightX + this.font.width(rightLabelText + rightValue) <= x + totalWidth) {
            graphics.drawString(this.font, rightLabelText, rightX, y, rightLabelColor);
            graphics.drawString(this.font, rightValue, rightX + this.font.width(rightLabelText), y, rightValueColor);
        }
    }

    private void renderJsonBox(GuiGraphics graphics, ResourceLocation sectionId, List<String> lines, String rawJson, int x1, int y1, int x2, int mouseX, int mouseY) {
        int y2 = y1 + lines.size() * LINE_HEIGHT + JSON_BOX_EXTRA_HEIGHT;

        graphics.fill(x1, y1, x2, y2, COLOR_JSON_BOX_BACKGROUND);
        graphics.fill(x1, y1, x2, y1 + 1, COLOR_JSON_BOX_BORDER);
        graphics.fill(x1, y2 - 1, x2, y2, COLOR_JSON_BOX_BORDER);
        graphics.fill(x1, y1, x1 + 1, y2, COLOR_JSON_BOX_BORDER);
        graphics.fill(x2 - 1, y1, x2, y2, COLOR_JSON_BOX_BORDER);

        int jsonTextY = y1 + JSON_BOX_VERTICAL_PADDING;
        for (String line : lines) {
            renderHighlightedJsonLine(graphics, line, x1 + JSON_BOX_TEXT_PADDING, jsonTextY);
            jsonTextY += LINE_HEIGHT;
        }

        int copyX = x2 - COPY_BUTTON_WIDTH - COPY_BUTTON_MARGIN;
        int copyY = y2 - COPY_BUTTON_HEIGHT - COPY_BUTTON_MARGIN;

        boolean hovered = mouseX >= copyX && mouseX < copyX + COPY_BUTTON_WIDTH && mouseY >= copyY && mouseY < copyY + COPY_BUTTON_HEIGHT;

        graphics.fill(copyX, copyY, copyX + COPY_BUTTON_WIDTH, copyY + COPY_BUTTON_HEIGHT, hovered ? COLOR_COPY_BUTTON_HOVER_BACKGROUND : COLOR_COPY_BUTTON_BACKGROUND);

        graphics.fill(copyX, copyY, copyX + COPY_BUTTON_WIDTH, copyY + 1, COLOR_COPY_BUTTON_BORDER);
        graphics.fill(copyX, copyY + COPY_BUTTON_HEIGHT - 1, copyX + COPY_BUTTON_WIDTH, copyY + COPY_BUTTON_HEIGHT, COLOR_COPY_BUTTON_BORDER);
        graphics.fill(copyX, copyY, copyX + 1, copyY + COPY_BUTTON_HEIGHT, COLOR_COPY_BUTTON_BORDER);
        graphics.fill(copyX + COPY_BUTTON_WIDTH - 1, copyY, copyX + COPY_BUTTON_WIDTH, copyY + COPY_BUTTON_HEIGHT, COLOR_COPY_BUTTON_BORDER);

        Component copyText = Component.translatable("screen.tabsections.debug.copy_json");
        int copyTextX = copyX + (COPY_BUTTON_WIDTH - this.font.width(copyText)) / 2;
        int copyTextY = copyY + 3;

        graphics.drawString(this.font, copyText, copyTextX, copyTextY, COLOR_COPY_BUTTON_TEXT);

        this.copyJsonHitboxes.add(new CopyJsonHitbox(copyX, copyY, COPY_BUTTON_WIDTH, COPY_BUTTON_HEIGHT, rawJson));
    }

    private void renderErrorBox(GuiGraphics graphics, String errorText, int x1, int y1, int x2) {
        int y2 = y1 + LINE_HEIGHT + JSON_BOX_EXTRA_HEIGHT;

        graphics.fill(x1, y1, x2, y2, COLOR_ERROR_BOX_BACKGROUND);
        graphics.fill(x1, y1, x2, y1 + 1, COLOR_ERROR_BOX_BORDER);
        graphics.fill(x1, y2 - 1, x2, y2, COLOR_ERROR_BOX_BORDER);
        graphics.fill(x1, y1, x1 + 1, y2, COLOR_ERROR_BOX_BORDER);
        graphics.fill(x2 - 1, y1, x2, y2, COLOR_ERROR_BOX_BORDER);

        graphics.drawString(this.font, errorText, x1 + JSON_BOX_TEXT_PADDING, y1 + JSON_BOX_VERTICAL_PADDING, COLOR_ERROR_TEXT);
    }

    private void renderBannerPreview(GuiGraphics graphics, TabSection section, int x, int y, boolean hovering) {
        Font font = this.font;

        graphics.blit(SectionHeaderStyle.FRAME_TEXTURE, x, y, 0.0F, 0.0F, BANNER_W, BANNER_H, BANNER_W, BANNER_H);
        drawAnimatedOrStaticBanner(graphics, section, x, y, hovering);

        int textWidth = font.width(section.title());
        int labelX1 = x + 2;
        int labelY1 = y + 2;
        int labelX2 = labelX1 + 8 + textWidth;
        int labelY2 = y + BANNER_H - 2;

        graphics.fill(labelX1, labelY1, labelX2, labelY2, section.textBackgroundColor());
        graphics.drawString(font, section.title(), x + 5, y + 5, section.textColor(), section.textShadow());
    }

    private void drawAnimatedOrStaticBanner(GuiGraphics graphics, TabSection section, int x, int y, boolean hovering) {
        TabSectionSpriteAnimation animation = section.visuals().spriteAnimation();

        if (animation != null && animation.frames() > 1) {
            BannerAnimationState state = getPreviewAnimationState(section);
            long previewTicks = getPreviewAnimationTicks();

            if (animation.animateOnHover()) {
                state.setPaused(!hovering, previewTicks);
            } else {
                state.setPaused(false, previewTicks);
            }

            int frame = state.getFrame(previewTicks, animation.frames(), animation.frameTime());
            int u = 0;
            int v = frame * BANNER_H;

            graphics.blit(section.bannerTexture(), x, y, (float) u, (float) v, BANNER_W, BANNER_H, BANNER_W, BANNER_H * animation.frames());
        } else {
            graphics.blit(section.bannerTexture(), x, y, 0.0F, 0.0F, BANNER_W, BANNER_H, BANNER_W, BANNER_H);
        }
    }

    private BannerAnimationState getPreviewAnimationState(TabSection section) {
        return this.previewAnimationStates.computeIfAbsent(section.id(), id -> new BannerAnimationState());
    }

    private long getPreviewAnimationTicks() {
        long elapsedMillis = Math.max(0L, this.lastRenderMillis - this.animationStartMillis);
        return elapsedMillis / 50L;
    }

    private SectionRenderData buildRenderData(TabSection section) {
        String modId = section.id().getNamespace();
        String modName = resolveModDisplayName(modId);

        String sectionDisplayId = section.id().getPath();
        String parentDisplayName = resolveParentTabDisplayName(section);
        String parentTabId = formatParentId(section);
        String animationSummary = buildAnimationSummary(section);

        JsonLoadResult jsonLoadResult = loadPrettyJson(section);
        List<String> jsonLines = jsonLoadResult.prettyJson() != null ? splitJsonLines(jsonLoadResult.prettyJson()) : List.of();

        return new SectionRenderData(modName, modId, parentDisplayName, parentTabId, sectionDisplayId, animationSummary, jsonLines, jsonLoadResult.prettyJson(), jsonLoadResult.errorMessage());
    }

    private String resolveParentTabDisplayName(TabSection section) {
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(section.parentTabId());

        if (tab == null) {
            return prettifyIdPath(section.parentTabId().getPath());
        }

        return tab.getDisplayName().getString();
    }

    private String prettifyIdPath(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }

        String[] parts = path.split("[_\\-/]");
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }

            if (!builder.isEmpty()) {
                builder.append(' ');
            }

            builder.append(Character.toUpperCase(part.charAt(0)));

            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }

        return builder.toString();
    }

    private String formatParentId(TabSection section) {
        ResourceLocation parent = section.parentTabId();
        return parent.getPath();
    }

    private String buildAnimationSummary(TabSection section) {
        TabSectionSpriteAnimation animation = section.visuals().spriteAnimation();

        if (animation == null) {
            return Component.translatable("screen.tabsections.debug.animation.no").getString();
        }

        return Component.translatable("screen.tabsections.debug.animation.yes", animation.frames(), animation.frameTime(),
                Component.translatable(animation.animateOnHover() ? "screen.tabsections.debug.value.yes" : "screen.tabsections.debug.value.no")).getString();
    }

    private String resolveModDisplayName(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getDisplayName())
                .filter(name -> !name.isBlank())
                .orElse(modId);
    }

    private JsonLoadResult loadPrettyJson(TabSection section) {
        ResourceLocation jsonLocation = ResourceLocation.fromNamespaceAndPath(section.id().getNamespace(), "tabsections/" + section.id().getPath() + ".json");

        try {
            Optional<Resource> resourceOpt = Minecraft.getInstance().getResourceManager().getResource(jsonLocation);

            if (resourceOpt.isEmpty()) {
                return new JsonLoadResult(null, "ERROR: Missing JSON source for: " + jsonLocation);
            }

            try (InputStream stream = resourceOpt.get().open();
                 Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                JsonElement parsed = JsonParser.parseReader(reader);
                return new JsonLoadResult(PRETTY_GSON.toJson(parsed), null);
            }
        } catch (Exception e) {
            return new JsonLoadResult(null, "ERROR: Failed to read JSON source for: " + jsonLocation);
        }
    }

    private List<String> splitJsonLines(String prettyJson) {
        List<String> lines = new ArrayList<>();
        int maxWidth = this.width - (OUTER_MARGIN * 2) - (BLOCK_PADDING * 2) - 12;

        String[] rawLines = prettyJson.replace("\r", "").split("\n");
        for (String rawLine : rawLines) {
            if (rawLine.isEmpty()) {
                lines.add("");
                continue;
            }

            String remaining = rawLine;
            while (!remaining.isEmpty()) {
                if (this.font.width(remaining) <= maxWidth) {
                    lines.add(remaining);
                    break;
                }

                String fitted = this.font.plainSubstrByWidth(remaining, maxWidth);
                if (fitted.isEmpty()) {
                    lines.add(remaining);
                    break;
                }

                lines.add(fitted);
                remaining = remaining.substring(fitted.length());
            }
        }

        return lines;
    }

    private void renderHighlightedJsonLine(GuiGraphics graphics, String line, int x, int y) {
        List<JsonTokenPart> parts = highlightJsonLine(line);

        int drawX = x;
        for (JsonTokenPart part : parts) {
            if (!part.text().isEmpty()) {
                graphics.drawString(this.font, part.text(), drawX, y, part.color());
                drawX += this.font.width(part.text());
            }
        }
    }

    private List<JsonTokenPart> highlightJsonLine(String line) {
        List<JsonTokenPart> parts = new ArrayList<>();

        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);

            if (Character.isWhitespace(c)) {
                int start = i;
                while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                parts.add(new JsonTokenPart(line.substring(start, i), COLOR_JSON_DEFAULT));
                continue;
            }

            if (c == '{' || c == '}' || c == '[' || c == ']') {
                parts.add(new JsonTokenPart(String.valueOf(c), COLOR_JSON_BRACES));
                i++;
                continue;
            }

            if (c == ':' || c == ',') {
                parts.add(new JsonTokenPart(String.valueOf(c), COLOR_JSON_PUNCTUATION));
                i++;
                continue;
            }

            if (c == '"') {
                int start = i;
                i++;

                boolean escaped = false;
                while (i < line.length()) {
                    char current = line.charAt(i);

                    if (current == '"' && !escaped) {
                        i++;
                        break;
                    }

                    if (current == '\\' && !escaped) {
                        escaped = true;
                    } else {
                        escaped = false;
                    }

                    i++;
                }

                String quoted = line.substring(start, i);

                int j = i;
                while (j < line.length() && Character.isWhitespace(line.charAt(j))) {
                    j++;
                }

                boolean isKey = j < line.length() && line.charAt(j) == ':';
                parts.add(new JsonTokenPart(quoted, isKey ? COLOR_JSON_KEY : COLOR_JSON_STRING));
                continue;
            }

            if (Character.isDigit(c) || c == '-') {
                int start = i;
                i++;

                while (i < line.length()) {
                    char current = line.charAt(i);
                    if (Character.isDigit(current) || current == '.' || current == 'e' || current == 'E' || current == '+' || current == '-') {
                        i++;
                    } else {
                        break;
                    }
                }

                parts.add(new JsonTokenPart(line.substring(start, i), COLOR_JSON_NUMBER));
                continue;
            }

            if (line.startsWith("true", i)) {
                parts.add(new JsonTokenPart("true", COLOR_JSON_BOOLEAN));
                i += 4;
                continue;
            }

            if (line.startsWith("false", i)) {
                parts.add(new JsonTokenPart("false", COLOR_JSON_BOOLEAN));
                i += 5;
                continue;
            }

            if (line.startsWith("null", i)) {
                parts.add(new JsonTokenPart("null", COLOR_JSON_NULL));
                i += 4;
                continue;
            }

            parts.add(new JsonTokenPart(String.valueOf(c), COLOR_JSON_DEFAULT));
            i++;
        }

        return parts;
    }

    private int computeBlockHeight(SectionRenderData data) {
        int metadataLines = 4;
        int metadataHeight = 12 + (metadataLines * LINE_HEIGHT);

        int bannerSectionHeight = BANNER_SECTION_TOP_GAP + LINE_HEIGHT + BANNER_LABEL_TO_RENDER_GAP + BANNER_H + BANNER_BOTTOM_GAP;

        int sourceLabelHeight = 12;

        int contentBoxHeight = data.missingJsonError() != null ? LINE_HEIGHT + JSON_BOX_EXTRA_HEIGHT : data.jsonLines().size() * LINE_HEIGHT + JSON_BOX_EXTRA_HEIGHT;

        return BLOCK_PADDING + metadataHeight + bannerSectionHeight + sourceLabelHeight + contentBoxHeight + BLOCK_PADDING;
    }

    private int getVisibleHeight() {
        return this.height - TOP_START - BOTTOM_RESERVED;
    }

    private void clampScroll() {
        int maxScroll = Math.max(0, this.contentHeight - getVisibleHeight());

        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }

        if (this.scrollOffset > maxScroll) {
            this.scrollOffset = maxScroll;
        }
    }

    private void drawPanel(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        graphics.fill(x1, y1, x2, y2, COLOR_PANEL_BACKGROUND);
        graphics.fill(x1, y1, x2, y1 + 1, COLOR_PANEL_BORDER);
        graphics.fill(x1, y2 - 1, x2, y2, COLOR_PANEL_BORDER);
        graphics.fill(x1, y1, x1 + 1, y2, COLOR_PANEL_BORDER);
        graphics.fill(x2 - 1, y1, x2, y2, COLOR_PANEL_BORDER);
    }

    private record SectionRenderData(String modDisplayName, String modId, String parentDisplayName, String parentTabId, String sectionDisplayId, String animationSummary, List<String> jsonLines, String rawJson, String missingJsonError) {}

    private record JsonLoadResult(String prettyJson, String errorMessage) {}

    private record JsonTokenPart(String text, int color) {}

    private record CopyJsonHitbox(int x, int y, int width, int height, String json) {
        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }
}