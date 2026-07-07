package net.adamgoodridge.sequencetrackplayer.statistic;

import lombok.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;

import java.util.Comparator;
import java.util.List;

@Getter
public class StatisticDto {
	private String date;
	private String dayOfWeek;
	private long secondsPlayed;
	private List<FeedStatDto> feedStats;

	public record FeedStatDto(String feedName, long secondsPlayed) {}

	public StatisticDto(Statistic statistic) {
		TimeUtils timeUtils = TimeUtils.getInstance();
		this.dayOfWeek = timeUtils.dateToWeekDay(statistic.getDate());
		this.date = timeUtils.dateToDate(statistic.getDate());
		this.secondsPlayed = statistic.getSecondsPlayed();
		createFeedStats(statistic.getFeedStats());
	}
	private void createFeedStats(List<FeedStatSummary> feedStatSummaries) {
		if (feedStatSummaries == null || feedStatSummaries.isEmpty()) {
			this.feedStats = List.of();
		} else {
			this.feedStats = feedStatSummaries.stream()
					.sorted(Comparator.comparingLong(FeedStatSummary::getSecondsPlayed).reversed())
					.map(f -> new FeedStatDto(f.getFeedName(), f.getSecondsPlayed()))
					.toList();
		}
	}
}
