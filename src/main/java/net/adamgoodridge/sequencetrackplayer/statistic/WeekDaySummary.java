package net.adamgoodridge.sequencetrackplayer.statistic;

public record WeekDaySummary(String name, long totalSeconds, double percentage) {
	public static WeekDaySummary create(String name, WeekDayBreakdown weekDayBreakdown) {
		return new WeekDaySummary(name,
				weekDayBreakdown.totalSeconds(), weekDayBreakdown.percentage());
	}
}