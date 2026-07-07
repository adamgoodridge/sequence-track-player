package net.adamgoodridge.sequencetrackplayer.statistic;

import lombok.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.*;
import java.util.*;

@Data
public class Statistic {

	//to find by a number
	@Id
	private long id;
	@Field
	private Date date;
	@Field
	private long secondsPlayed;
	@Field
	private List<FeedStatSummary> feedStats = new ArrayList<>();

	public void addSecondsPlayed(long seconds, String feedName) {
		this.secondsPlayed += seconds;
		if (feedName != null && !feedName.isBlank()) {
			if (feedStats == null) feedStats = new ArrayList<>();
			populateFeedStat(seconds, feedName);
		}
	}

	private void populateFeedStat(long seconds, String feedName) {
		Optional<FeedStatSummary> feedStat = feedStats.stream()
				.filter(f -> f.getFeedName().equals(feedName))
				.findFirst();
		if (feedStat.isPresent()) {
			FeedStatSummary existing = feedStat.get();
			feedStats.remove(existing);
			feedStats.add(new FeedStatSummary(feedName, existing.getSecondsPlayed() + seconds));
		} else {
			feedStats.add(new FeedStatSummary(feedName, seconds));
		}
	}

	public StatisticDto toDto() {
		return new StatisticDto(this);
	}

	public String getMonth() {
		return TimeUtils.getInstance().dateToMonth(date);
	}
	public String getDayOfWeek() {
		return TimeUtils.getInstance().dateToWeekDay(date);
	}
}
