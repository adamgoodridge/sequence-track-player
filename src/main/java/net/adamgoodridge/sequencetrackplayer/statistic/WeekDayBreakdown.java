package net.adamgoodridge.sequencetrackplayer.statistic;

public record WeekDayBreakdown(long totalSeconds, double percentage) {

	public static class WeekDayBreakdownBuilder {
		private long totalSeconds;
		private long weekTotal;


		public WeekDayBreakdownBuilder setTotalSeconds(long totalSeconds) {
			this.totalSeconds = totalSeconds;
			return this;
		}

		public WeekDayBreakdownBuilder setWeekTotal(long weekTotal) {
			this.weekTotal = weekTotal;
			return this;
		}

		public WeekDayBreakdown build() {
			double percentage = calculatePercentage(weekTotal, totalSeconds);
			return new WeekDayBreakdown(totalSeconds, percentage);
		}
	}
	private static double calculatePercentage(long weekTotal, long totals) {
		return weekTotal > 0 ? round2dp(totals * 100.0 / weekTotal) : 0.0;
	}

	private static double round2dp(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
