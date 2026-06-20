package net.adamgoodridge.sequencetrackplayer.settings;

import lombok.*;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;

import java.time.LocalDate;

@Getter
public class PreferredRandomSettings {
    private static final String DAY_VALUE = "day";
    private static final String WILDCARD = "*";

    private DayOfWeek dayOfWeek;
    private final int time;
    private final boolean regularlyTrackChange;
    private final int trackCurrentCount;
    private final boolean isHolidayPeriod;
    private static final String CONTAINING_MONTH_REGEX =  ".*(January|February|March|April|May|June|July|August|September|October|November|December).*";



    private PreferredRandomSettings(Builder builder) {
        this.dayOfWeek = DayOfWeek.toDayOfWeek(builder.dayOfWeek);
        this.time = builder.time;
        this.regularlyTrackChange = builder.regularlyTrackChange;
        this.trackCurrentCount = builder.trackCurrentCount;
        this.isHolidayPeriod = shouldConsiderHolidayPeriod(builder.considerHolidayPeriod, builder.today);
    }
    private boolean shouldConsiderHolidayPeriod(HolidayPeriodConsideration consideration, LocalDate today) {
        if(consideration == HolidayPeriodConsideration.ALWAYS)
            return true;
        if(consideration == HolidayPeriodConsideration.NEVER)
            return false;
        return consideration.shouldConsiderHolidayPeriod(today);
    }

    public boolean shouldFilterByDayOfWeek(DataItem dataItem) {
        if(dayOfWeek == DayOfWeek.ALL)
            return false;
        return dataItem.getFileName().toLowerCase().contains(DAY_VALUE);
    }
    public static Builder builder() {
        return new Builder();
    }

    public boolean shouldFilterByMonth(DataItem dataItem) {
        return isHolidayPeriod && dataItem.getFileName().matches(CONTAINING_MONTH_REGEX);
    }

    public void gottendayOfWeek() {
        dayOfWeek = DayOfWeek.ALL;
    }


    public static class Builder {
        private String dayOfWeek = WILDCARD;
        private int time = -1;
        private boolean regularlyTrackChange = false;
        private int trackCurrentCount = 0;
        private HolidayPeriodConsideration considerHolidayPeriod = HolidayPeriodConsideration.getDefault();
        private LocalDate today = new LocalDateUtils().now();

        public Builder dayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek != null ? dayOfWeek.name() : WILDCARD;
            return this;
        }
        public Builder dayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek != null ? dayOfWeek : WILDCARD;
            return this;
        }

        public Builder time(int time) {
            this.time = time;
            return this;
        }
        public Builder considerHolidayPeriod(HolidayPeriodConsideration considerHolidayPeriod) {
            this.considerHolidayPeriod = considerHolidayPeriod;
            return this;
        }
        public Builder today(LocalDate today) {
            this.today = today;
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
