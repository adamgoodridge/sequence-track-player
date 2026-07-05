package net.adamgoodridge.sequencetrackplayer.statistic;

import java.util.Map;

public record MonthSummary(String name, long totalSeconds, Map<String, WeekDayBreakdown> weekDayBreakdown) {}