package com.ziver.tabsections.tabs.sections.data;

public class TabSectionJsonData {
    public String title_key;
    public String banner;
    public String text_color;
    public String label_color;
    public Boolean shadow;
    public SpriteAnimationJson sprite_animation;

    public static class SpriteAnimationJson {
        public Integer frame_time;
        public Integer frames;
        public Boolean animate_on_hover;
    }
}