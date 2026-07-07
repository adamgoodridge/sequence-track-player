package net.adamgoodridge.sequencetrackplayer.statistic;

import java.util.List;

public record WeekDayBreakdown(long totalSeconds, double percentage, List<FeedStatSummary> feedStats) {

	public static WeekDayBreakdown create(long weekTotal, long totalSeconds, List<FeedStatSummary> feedStats) {
		double percentage = calculatePercentage(weekTotal, totalSeconds);
		return new WeekDayBreakdown(totalSeconds, percentage, feedStats);
	}

	private static double calculatePercentage(long weekTotal, long totals) {
		return weekTotal > 0 ? round2dp(totals * 100.0 / weekTotal) : 0.0;
	}

	private static double round2dp(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
