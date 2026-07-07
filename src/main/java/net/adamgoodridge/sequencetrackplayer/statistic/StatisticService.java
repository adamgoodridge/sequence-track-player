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

	public void addSecondsPlayed(long seconds, String feedName) {
		Statistic statistic = getTodayStatistic(TodayStatisticPolicy.CREATE_IF_ABSENT);
		statistic.addSecondsPlayed(seconds, feedName);
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
		Map<String, DayAccumulator> accumulators = initAccumulators(days);
		for (Statistic stat : statisticRepository.findByDateBetween(fromDate, toDate))
			accumulators.get(stat.getDayOfWeek()).accumulate(stat);
		long weekTotal = accumulators.values().stream().mapToLong(DayAccumulator::totalSeconds).sum();
		List<WeekDaySummary> weekDaySummaries = new ArrayList<>();
		for (String day : days) {
			weekDaySummaries.add(toWeekDaySummary(day, accumulators.get(day), weekTotal));
		}
		return weekDaySummaries;
	}

	private WeekDaySummary toWeekDaySummary(String day, DayAccumulator acc, long weekTotal) {
		WeekDayBreakdown breakdown = WeekDayBreakdown.create(weekTotal, acc.totalSeconds(), List.of());
		return WeekDaySummary.create(day, breakdown, acc.feedStats());
	}

	public List<MonthSummary> getSummaryMonth() {
		List<String> months = Arrays.asList(ConstantText.MONTH_NAMES);
		List<String> days = Arrays.asList(ConstantText.DAYS_OF_WEEK);
		Map<String, DayAccumulator> monthAccumulators = initAccumulators(months);
		Map<String, Map<String, DayAccumulator>> monthDayAccumulators = initNestedAccumulators(months, days);
		for (Statistic stat : statisticRepository.findAll()) {
			String month = stat.getMonth();
			monthAccumulators.get(month).accumulate(stat);
			monthDayAccumulators.get(month).get(stat.getDayOfWeek()).accumulate(stat);
		}
		return months.stream()
				.map(month ->
						new MonthSummary.BuildMonthSummary()
								.name(month)
								.days(days)
								.monthAcc(monthAccumulators.get(month))
								.dayAccumulators(monthDayAccumulators.get(month))
								.build()
				)
								.toList();

	}


	private Map<String, DayAccumulator> initAccumulators(List<String> keys) {
		Map<String, DayAccumulator> map = new LinkedHashMap<>();
		keys.forEach(k -> map.put(k, new DayAccumulator()));
		return map;
	}

	private Map<String, Map<String, DayAccumulator>> initNestedAccumulators(List<String> outerKeys, List<String> innerKeys) {
		Map<String, Map<String, DayAccumulator>> map = new LinkedHashMap<>();
		outerKeys.forEach(outer -> map.put(outer, initAccumulators(innerKeys)));
		return map;
	}

	public List<Statistic> getStatisticsByDateRange(DateRange dateRange) {
		return statisticRepository.findByDateBetween(dateRange.startDate(), dateRange.endDate());
	}
}