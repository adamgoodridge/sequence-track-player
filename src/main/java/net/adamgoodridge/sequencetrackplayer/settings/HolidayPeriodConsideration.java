package net.adamgoodridge.sequencetrackplayer.settings;

import net.adamgoodridge.sequencetrackplayer.utils.*;

import java.time.*;

public enum HolidayPeriodConsideration {
	YES,
	NEVER,
	ALWAYS;

	public static HolidayPeriodConsideration fromString(String value) {
		if(value != null) {
			value = value.trim();
		}
		if(value == null)
			return NEVER;
		return switch (value.toUpperCase()) {
			case "YES" -> YES;
			case "ALWAYS" -> ALWAYS;
			default -> NEVER;
		};
	}

	public boolean shouldConsiderHolidayPeriod(LocalDate today) {
		if(this == ALWAYS)
			return true;
		if(this == NEVER)
			return false;
		return TimeUtils.isInHolidayPeriod(today);
	}
	public static HolidayPeriodConsideration getDefault() {
		return YES;
	}
}
