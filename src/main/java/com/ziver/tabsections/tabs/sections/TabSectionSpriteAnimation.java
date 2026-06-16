package com.ziver.tabsections.tabs.sections;

public class TabSectionSpriteAnimation {
    private final int frameTime;
    private final int frames;
    private final boolean animateOnHover;

    public TabSectionSpriteAnimation(int frameTime, int frames, boolean animateOnHover) {
        this.frameTime = Math.max(1, frameTime);
        this.frames = Math.max(1, frames);
        this.animateOnHover = animateOnHover;
    }

    public int frameTime() {
        return frameTime;
    }

    public int frames() {
        return frames;
    }

    public boolean animateOnHover() {
        return animateOnHover;
    }
}