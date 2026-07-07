package net.adamgoodridge.sequencetrackplayer.statistic;

import java.util.*;

public record MonthSummary(String name, long totalSeconds, Map<String, WeekDayBreakdown> weekDayBreakdown, List<FeedStatSummary> feedStats) {
	public static class BuildMonthSummary {
		private String name;
		private List<String> days;
		private DayAccumulator monthAcc;
		private Map<String, DayAccumulator> dayAccumulators;

		public BuildMonthSummary name(String name) {
			this.name = name;
			return this;
		}

		public BuildMonthSummary days(List<String> days) {
			this.days = days;
			return this;
		}

		public BuildMonthSummary monthAcc(DayAccumulator monthAcc) {
			this.monthAcc = monthAcc;
			return this;
		}

		public BuildMonthSummary dayAccumulators(Map<String, DayAccumulator> dayAccumulators) {
			this.dayAccumulators = dayAccumulators;
			return this;
		}

		public MonthSummary build() {
			Objects.requireNonNull(name, "name must not be null");
			Objects.requireNonNull(days, "days must not be null");
			Objects.requireNonNull(monthAcc, "monthAcc must not be null");
			Objects.requireNonNull(dayAccumulators, "dayAccumulators must not be null");

			long monthTotal = monthAcc.totalSeconds();
			Map<String, WeekDayBreakdown> breakdowns = new LinkedHashMap<>();
			for (String day : days) {
				DayAccumulator dayAcc = dayAccumulators.getOrDefault(day, new DayAccumulator());
				breakdowns.put(day, WeekDayBreakdown.create(monthTotal, dayAcc.totalSeconds(), dayAcc.feedStats()));
			}
			return new MonthSummary(name, monthTotal, breakdowns, monthAcc.feedStats());
		}
	}
}
