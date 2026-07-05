package net.adamgoodridge.sequencetrackplayer.statistic;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import java.util.*;

public class StatisticParameters {

	private static final int DEFAULT_NUMBER_OF_PREVIOUS_DAYS = 100;

	private final Integer days;
	private final Date from;
	private final Date to;

	public StatisticParameters(Integer days, String from, String to) {
		this.days = days;
		this.from = stringToDate(from);
		this.to = stringToDate(to);
	}

	public DateRange getDateRange() {
		StatisticFilterBy filter = resolveFilter();
		if (filter == StatisticFilterBy.EXPLICIT_DATE_RANGE)
			return buildExplicitDateRange();
		return buildRelativeDateRange(filter);
	}

	private StatisticFilterBy resolveFilter() {
		if (hasTooManyParameters())
			throw new InvalidStatisticsDateTooManyParametersError();
		if (isNoParameters())
			return StatisticFilterBy.DEFAULT_PREVIOUS_DAYS;
		if (days != null)
			return StatisticFilterBy.NUMBER_OF_PREVIOUS_DAYS;
		if (isToProvidedWithoutFrom())
			throw new FromDateMissingFromQueryParametersError();
		return StatisticFilterBy.EXPLICIT_DATE_RANGE;
	}

	private DateRange buildExplicitDateRange() {
		return new DateRange(from, resolveToDate());
	}

	private DateRange buildRelativeDateRange(StatisticFilterBy filter) {
		int subtractDays = filter == StatisticFilterBy.DEFAULT_PREVIOUS_DAYS
				? DEFAULT_NUMBER_OF_PREVIOUS_DAYS
				: days;
		Date fromDate = TimeUtils.getInstance().subtractDays(subtractDays);
		return new DateRange(fromDate, resolveToDate());
	}

	private Date resolveToDate() {
		return to != null ? to : TimeUtils.getInstance().getCurrentDate();
	}

	private boolean hasTooManyParameters() {
		return days != null && (from != null || to != null);
	}

	public boolean isNoParameters() {
		return days == null && from == null && to == null;
	}

	private boolean isToProvidedWithoutFrom() {
		return to != null && from == null;
	}

	private static Date stringToDate(String input) {
		return input != null ? TimeUtils.getInstance().dateFromString(input) : null;
	}
}

