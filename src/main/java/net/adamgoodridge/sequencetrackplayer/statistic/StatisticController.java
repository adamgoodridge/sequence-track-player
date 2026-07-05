package net.adamgoodridge.sequencetrackplayer.statistic;

import io.swagger.v3.oas.annotations.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/v1/statistic")
public class StatisticController {
	private final StatisticService statisticService;

	public StatisticController(StatisticService statisticService) {
		this.statisticService = statisticService;
	}
	@GetMapping("/")
	@ResponseBody
	public List<StatisticDto> getStatistics(@Parameter String toDate, @Parameter String fromDate, @Parameter Integer days) {
		StatisticParameters parameters = new StatisticParameters(days, fromDate, toDate);
		DateRange dateRange = parameters.getDateRange();
		List<Statistic> statistics = statisticService.getStatisticsByDateRange(dateRange);
		List<StatisticDto> statisticDtos = statistics.stream().map(StatisticDto::new).toList();
		return statisticDtos;
	}
	@GetMapping("/today")
	@ResponseBody
	public StatisticDto getTodayStatistic() {
		return statisticService.getTodayStatistic().toDto();
	}

	@GetMapping("/summary/week")
	@ResponseBody
	public List<WeekDaySummary> getWeekSummary(@Parameter String toDate, @Parameter String fromDate, @Parameter Integer days) {
		StatisticParameters parameters = new StatisticParameters(days, fromDate, toDate);
		return parameters.isNoParameters() ?
				statisticService.getSummaryWeekDay()
				: statisticService.getWeekSummary(parameters.getDateRange());
		}
	@GetMapping("/summary/month")
	@ResponseBody
	public List<MonthSummary> getMonthSummary() {
		return statisticService.getSummaryMonth();
	}
}
