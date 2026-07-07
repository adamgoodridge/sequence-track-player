package net.adamgoodridge.sequencetrackplayer.statistic;

import lombok.*;

@Data
@AllArgsConstructor
public class FeedStatSummary {
	String feedName;
	long secondsPlayed;
	public void addSecondsPlayed(long seconds) {
		this.secondsPlayed += seconds;
	}
}