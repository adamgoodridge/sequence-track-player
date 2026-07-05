package net.adamgoodridge.sequencetrackplayer.statistic;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class StatisticService {

	private final StatisticRepository statisticRepository;

	public StatisticService(StatisticRepository statisticRepository) {
		this.statisticRepository = statisticRepository;
	}

	public void addSecondsPlayed(long seconds) {
		Statistic statistic = getTodayStatistic(TodayStatisticPolicy.CREATE_IF_ABSENT);
		statistic.addSecondsPlayed(seconds);
		statisticRepository.save(statistic);
	}
	public Statistic getTodayStatistic(TodayStatisticPolicy policy) {
		Date currentDate = TimeUtils.getInstance().getCurrentDate();
		Statistic found = statisticRepository.findByDate(currentDate);
		if (found == null && policy == TodayStatisticPolicy.CREATE_IF_ABSENT)
			found = createAndSaveStatistic(currentDate);
		if (found == null)
			throw new TodayStatisticNotFoundError();
		return found;
	}

	private Statistic createAndSaveStatistic(Date date) {
		Statistic statistic = new Statistic();
		statistic.setId(date.getTime());
		statistic.setDate(date);
		statisticRepository.save(statistic);
		return statistic;
	}
	public Statistic getTodayStatistic() {
		return getTodayStatistic(TodayStatisticPolicy.FIND_ONLY);
	}

	public List<WeekDaySummary> getSummaryWeekDay() {
		return getSummaryWeekDay(TimeUtils.getInstance().getStartOfWeek(), TimeUtils.getInstance().getCurrentDate());
	}
	public List<WeekDaySummary> getWeekSummary(DateRange dateRange) {
		List<WeekDaySummary> allDays = getSummaryWeekDay(dateRange.startDate(), dateRange.endDate());
		return TimeUtils.removeNotRequiredWeekDay(dateRange, allDays);
	}
	private List<WeekDaySummary> getSummaryWeekDay(Date fromDate, Date toDate) {
		List<String> days = Arrays.asList(ConstantText.DAYS_OF_WEEK);
		long[] totals = accumulateDayTotals(statisticRepository.findByDateBetween(fromDate, toDate), days);
		return buildWeekDaySummaries(days, totals);
	}

	private long[] accumulateDayTotals(List<Statistic> stats, List<String> days) {
		long[] totals = new long[days.size()];
		for (Statistic stat : stats)
			totals[days.indexOf(stat.getDayOfWeek())] += stat.getSecondsPlayed();
		return totals;
	}

	private List<WeekDaySummary> buildWeekDaySummaries(List<String> days, long[] totals) {
		long weekTotal = Arrays.stream(totals).sum();
		List<WeekDaySummary> result = new ArrayList<>();
		for (int i = 0; i < days.size(); i++) {
			WeekDayBreakdown breakdown = new WeekDayBreakdown.WeekDayBreakdownBuilder()
					.setTotalSeconds(totals[i])
					.setWeekTotal(weekTotal)
					.build();
			result.add(WeekDaySummary.create(days.get(i), breakdown));
		}
		return result;
	}


	public List<MonthSummary> getSummaryMonth() {
		List<String> months = Arrays.asList(ConstantText.MONTH_NAMES);
		List<String> days   = Arrays.asList(ConstantText.DAYS_OF_WEEK);
		long[] monthTotals      = new long[months.size()];
		long[][] monthDayTotals = new long[months.size()][days.size()];
		accumulateMonthStats(months, days, monthTotals, monthDayTotals);
		return buildMonthSummaries(months, days, monthTotals, monthDayTotals);
	}

	private void accumulateMonthStats(List<String> months, List<String> days,
	                                   long[] monthTotals, long[][] monthDayTotals) {
		for (Statistic stat : statisticRepository.findAll()) {
			int mIdx = months.indexOf(stat.getMonth());
			int dIdx = days.indexOf(stat.getDayOfWeek());
			monthTotals[mIdx] += stat.getSecondsPlayed();
			monthDayTotals[mIdx][dIdx] += stat.getSecondsPlayed();
		}
	}

	private List<MonthSummary> buildMonthSummaries(List<String> months, List<String> days,
	                                                long[] monthTotals, long[][] monthDayTotals) {
		List<MonthSummary> result = new ArrayList<>();
		for (int m = 0; m < months.size(); m++)
			result.add(buildMonthSummary(months.get(m), monthTotals[m], monthDayTotals[m], days));
		return result;
	}

	private MonthSummary buildMonthSummary(String month, long monthTotal, long[] dayTotals, List<String> days) {
		return new MonthSummary(month, monthTotal, buildWeekDayBreakdown(monthTotal, dayTotals, days));
	}

	private Map<String, WeekDayBreakdown> buildWeekDayBreakdown(long monthTotal, long[] totals, List<String> days) {
		Map<String, WeekDayBreakdown> breakdowns = new LinkedHashMap<>();
		for (int d = 0; d < days.size(); d++) {
			WeekDayBreakdown breakdown = new WeekDayBreakdown.WeekDayBreakdownBuilder()
					.setTotalSeconds(totals[d])
					.setWeekTotal(monthTotal)
					.build();
			breakdowns.put(days.get(d), breakdown);
		}
		return breakdowns;
}

	public List<Statistic> getStatisticsByDateRange(DateRange dateRange) {
		return statisticRepository.findByDateBetween(dateRange.startDate(), dateRange.endDate());
	}

}