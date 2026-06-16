package com.ziver.tabsections.tabs.sections.state;

public class BannerAnimationState {

    private long startTime = -1;
    private long pausedOffset = 0;
    private boolean paused = false;

    public int getFrame(long gameTime, int frames, int frameTime) {
        if (startTime == -1) {
            startTime = gameTime;
        }

        long effectiveTime = paused ? pausedOffset : (gameTime - startTime);

        return (int) ((effectiveTime / frameTime) % frames);
    }

    public void setPaused(boolean pause, long gameTime) {
        if (pause && !paused) {
            pausedOffset = gameTime - startTime;
            paused = true;
        } else if (!pause && paused) {
            startTime = gameTime - pausedOffset;
            paused = false;
        }
    }

    public void reset() {
        startTime = -1;
        pausedOffset = 0;
        paused = false;
    }
}