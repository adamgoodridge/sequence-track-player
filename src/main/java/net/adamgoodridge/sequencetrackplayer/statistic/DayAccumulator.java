package net.adamgoodridge.sequencetrackplayer.statistic;


import java.util.*;

public class DayAccumulator {
	private long totalSeconds;
	private final List<FeedStatSummary> feedStats = new ArrayList<>();

	void accumulate(Statistic stat) {
		totalSeconds += stat.getSecondsPlayed();
		for (FeedStatSummary incoming : stat.getFeedStats()) {
			FeedStatSummary feedStatSummary = feedStats.stream()
					.filter(f -> f.getFeedName().equals(incoming.getFeedName()))
					.findFirst().orElse(null);
			if(feedStatSummary != null) {
				feedStats.remove(feedStatSummary);
				feedStatSummary.addSecondsPlayed(incoming.getSecondsPlayed());
				feedStats.add(feedStatSummary);
			} else {
				feedStats.add(incoming);
			}
		}
	}

	long totalSeconds() { return totalSeconds; }
	List<FeedStatSummary> feedStats() { return feedStats; }
}