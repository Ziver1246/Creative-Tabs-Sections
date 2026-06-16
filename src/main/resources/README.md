# Creative Tab Sections

Adds **sectioned layouts** to Minecraft Creative Tabs, allowing mods to group items into visually distinct categories with headers, banners, and optional animations.

This system replaces the default flat item list with a structured layout while preserving compatibility with search.

---

## What problem does this solve?

Vanilla Creative Tabs are flat and unstructured. For mods with large item sets, this leads to:

- Poor discoverability
- Visual clutter
- No semantic grouping
- No way to inject contextual UI (labels, categories, etc.)

This mod introduces:

- **Logical grouping of items into sections**
- **Custom headers per section**
- **Visual separation between groups**
- **Optional animated banners**
- **Preserved search behavior (no empty slots pollution)**

---

## Design philosophy

- **Code-driven structure** (sections and items are defined in code)
- **Data-driven visuals** (appearance is defined via JSON)
- **Non-invasive** (only affects tabs that explicitly register sections)
- **Composable** (can be embedded or copied into another mod)

---

## License / Usage

This project is intended to be permissive.

- You are allowed to **copy the code directly into your mod**
- You may **modify it freely** to fit your needs
- You may **avoid using it as a dependency entirely**

This is intentional to allow tight integration into mods without forcing dependency chains.

---

## Core Concepts

### Section

A section represents a group of items inside a Creative Tab.

Defined by:
- Parent tab
- Unique ID
- Priority (ordering)
- Item suppliers
- Visual configuration (via JSON)

---

## Developer Tools

Tab Sections includes a built-in debug screen intended for development and inspection of registered tab sections.

It provides:
- Section metadata (mod, parent tab, priority)
- Banner preview with animation support
- JSON source visualization with syntax highlighting
- Clipboard copy support

This tool is disabled by default and can be enabled via the client config.

> Note: This feature is intended for developers and advanced users. It is not part of the standard gameplay experience.

---

## Registering Sections

Sections must be registered in code.

Example:

```java
public class RegTabSections {

    public static final TabSection MATERIALS =
            TabSectionRegistry.register(
                    ModTabs.MY_TAB,
                    ModId.id("materials"),
                    0
            );

    public static final TabSection TOOLS =
            TabSectionRegistry.register(
                    ModTabs.MY_TAB,
                    ModId.id("tools"),
                    1
            );

    public static final TabSection SPECIAL_ITEMS =
            TabSectionRegistry.register(
                    ModTabs.MY_TAB,
                    ModId.id("special_items"),
                    2
            );
}
```

### Arguments

```java
TabSectionRegistry.register(parentTabId, sectionId, priority)
```

- **parentTabId** → `ResourceLocation` of the Creative Tab
- **sectionId** → unique identifier for the section
- **priority** → integer used to sort sections (ascending)

---

## Ordering and Priority

- Sections are sorted by **priority (ascending)**
- Lower value = appears first
- Sorting is stable (insertion order is preserved for equal priorities)

### Duplicate priority

- Allowed
- Order falls back to **registration order**
- No override or merge occurs

### Duplicate section IDs

- Ignored
- First registration wins
- Warning is logged

---

## Adding Items to Sections

### Basic items

```java
section.add(() -> ModItems.MY_ITEM);
```

### Multiple items

```java
section.add(
    () -> ModItems.ITEM_1,
    () -> ModItems.ITEM_2
);
```

### Custom stacks (dynamic data)

```java
section.addStack(() -> {
    ItemStack stack = new ItemStack(item.get());
    // mutate stack (NBT, capabilities, etc.)
    return stack;
});
```

This is important for:
- Variants
- Data-driven items
- Capability-based items

---

## Layout Behavior

Internally, the layout is rebuilt:

- Items are grouped per section
- Each section:
    - Occupies full rows (9 slots per row)
    - Is padded with empty slots if needed
- A full empty row is inserted between sections

This enables:
- Clean visual separation
- Precise header alignment

### Search tab

- Contains **only real items**
- No empty padding
- No visual sections

---

## JSON Configuration (Visuals)

Each section can define its visuals via JSON.

### Location

```
assets/<namespace>/tabsections/<section_path>.json
```

Example:

```
assets/astralsorcery/tabsections/constellation_scrolls.json
```

---

### Structure

```json
{
  "title_key": "tooltip.astralsorcery.constellations",
  "banner": "astralsorcery:textures/gui/banner.png",
  "text_color": "#FFFFFFFF",
  "label_color": "#AA550000",
  "shadow": false,
  "sprite_animation": {
    "frame_time": 2,
    "frames": 4,
    "animate_on_hover": true
  }
}
```

---

### Fields

| Field | Type | Description |
|------|------|------------|
| `title_key` | string | Translation key for section title |
| `banner` | string | Texture path |
| `text_color` | hex string | Text color |
| `label_color` | hex string | Background label color |
| `shadow` | boolean | Text shadow |
| `sprite_animation` | object | Optional animation |

---

### Defaults / Fallbacks

If a field is missing or invalid:

- `title_key` → falls back to section ID
- `banner` → missing texture
- colors → fallback values
- animation → disabled

Errors do **not crash** the game; warnings are logged.

---

## Banner Textures

### Static banner

A single image:

```
162 x 18 pixels
```

---

### Animated banner (sprite sheet)

Animation is vertical.

Each frame:

```
Width:  162 px  
Height: 18 px  
```

Stack frames **vertically**:

```
Frame 0  
Frame 1  
Frame 2  
Frame 3  
...
```

Total texture size:

```
162 x (18 * frames)
```

---

### Example (4 frames)

```
162 x 72
```

---

### Animation Behavior

- `frame_time` → ticks per frame
- `frames` → number of frames
- `animate_on_hover`:
    - `true` → animation only plays while hovering
    - `false` → always animates

---

## Rendering Notes

- Headers are rendered dynamically based on scroll position
- Only visible sections are drawn
- Hover detection is per-row
- Animation state is tracked per section per tab

---

## Resource Reload

- JSON is reloaded via resource reload (F3+T)
- No restart required

---

## Limitations / Assumptions

- Only affects tabs that register sections
- Global render state assumes a single active screen
- No config system (currently)
- Sections are **not defined via JSON**, only visuals are
- Item content is entirely code-driven

---

## Integration Pattern

Typical flow:

1. Register sections
2. Populate them with items
3. Provide JSON visuals

Example:

```java
public class TabSectionsOrder {

    public static void register() {
        section.add(() -> ModItems.ITEM);

        section.addStack(() -> {
            ItemStack stack = new ItemStack(ModItems.ITEM.get());
            // modify stack
            return stack;
        });
    }
}
```

---

## When to embed instead of depend

You may want to copy the system instead of depending on it if:

- You need deep customization
- You want zero external dependencies
- You want to modify layout or rendering behavior

This is supported by design.

---

## Summary

This system provides:

- Structured Creative Tabs
- Flexible item injection
- Visual customization via JSON
- Optional animated headers

Without requiring invasive changes to the rest of your mod.