package net.adamgoodridge.sequencetrackplayer.constanttext;

import net.adamgoodridge.sequencetrackplayer.exceptions.InvalidDayOfWeekError;

public enum DayOfWeek {
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY,
	SUNDAY,
	ALL;

	public static DayOfWeek toDayOfWeek(String value) {
		if (value == null)
			return ALL;


		return switch (value.trim().toUpperCase()) {
			case "*" -> ALL;
			case "ALL" -> ALL;
			case "MONDAY" -> MONDAY;
			case "TUESDAY" -> TUESDAY;
			case "WEDNESDAY" -> WEDNESDAY;
			case "THURSDAY" -> THURSDAY;
			case "FRIDAY" -> FRIDAY;
			case "SATURDAY" -> SATURDAY;
			case "SUNDAY" -> SUNDAY;
			default -> throw new InvalidDayOfWeekError(value);
		};
	}
}
