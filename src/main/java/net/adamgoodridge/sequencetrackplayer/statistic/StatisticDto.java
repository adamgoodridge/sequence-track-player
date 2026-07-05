package net.adamgoodridge.sequencetrackplayer.statistic;

import lombok.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.thymeleaf.util.*;

import java.util.*;
@Getter
public class StatisticDto {
	private String date;
	private String dayOfWeek;
	private long secondsPlayed;


	public StatisticDto(Statistic statistic) {
		TimeUtils timeUtils = TimeUtils.getInstance();
		this.dayOfWeek = timeUtils.dateToWeekDay(statistic.getDate());
		this.date = timeUtils.dateToDate(statistic.getDate());
		this.secondsPlayed = statistic.getSecondsPlayed();
	}
}
