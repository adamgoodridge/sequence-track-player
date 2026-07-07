package net.adamgoodridge.sequencetrackplayer.statistic;

import java.util.List;

public record WeekDaySummary(String name, long totalSeconds, double percentage, List<FeedStatSummary> feedStats) {
	public static WeekDaySummary create(String name, WeekDayBreakdown weekDayBreakdown, List<FeedStatSummary> feedStats) {
		return new WeekDaySummary(name, weekDayBreakdown.totalSeconds(), weekDayBreakdown.percentage(), feedStats);
	}
}
