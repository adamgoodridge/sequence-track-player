package net.adamgoodridge.sequencetrackplayer.settings;

import net.adamgoodridge.sequencetrackplayer.feeder.*;

public class PreferredRandomSettings {
    private String day;
    private final int time;
    private final boolean regularlyTrackChange;
    private final int trackCurrentCount;

    private PreferredRandomSettings(Builder builder) {
        this.day = builder.day;
        this.time = builder.time;
        this.regularlyTrackChange = builder.regularlyTrackChange;
        this.trackCurrentCount = builder.trackCurrentCount;
    }

    public String getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public boolean isRegularlyTrackChange() {
        return regularlyTrackChange;
    }

    public int getTrackCurrentCount() {
        return trackCurrentCount;
    }
    /**
     * Marks the day as "gotten", meaning it will be reset to "*" down the directory structure.
     */
    public void gottenDay() {
        day = "*";
    }

    public boolean shouldFilterByDay(DataItem dataItem) {
        if(day.equals("*")) {
            return false;
        }
        return dataItem.getFileName().contains(day);
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String day = "*";
        private int time = -1;
        private boolean regularlyTrackChange = false;
        private int trackCurrentCount = 0;

        public Builder day(String day) {
            this.day = day != null ? day : "*";
            return this;
        }

        public Builder time(int time) {
            this.time = time;
            return this;
        }

        public Builder regularlyTrackChange(boolean regularlyTrackChange) {
            this.regularlyTrackChange = regularlyTrackChange;
            return this;
        }

        public Builder trackCurrentCount(int trackCurrentCount) {
            this.trackCurrentCount = Math.max(0, trackCurrentCount);
            return this;
        }

        public PreferredRandomSettings build() {
            return new PreferredRandomSettings(this);
        }
    }
}
