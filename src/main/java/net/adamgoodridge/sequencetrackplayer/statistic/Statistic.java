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
	public void addSecondsPlayed(long seconds) {
		this.secondsPlayed += seconds;
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
