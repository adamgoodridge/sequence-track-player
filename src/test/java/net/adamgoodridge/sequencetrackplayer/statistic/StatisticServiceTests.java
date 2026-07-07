package net.adamgoodridge.sequencetrackplayer.statistic;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.constanttext.ConstantText;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.TodayStatisticNotFoundError;
import net.adamgoodridge.sequencetrackplayer.utils.TimeUtils;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticServiceTests extends AbstractSpringBootTest {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private StatisticRepository statisticRepository;

    private static final Date FIXED_DATE = new Date(1741737600000L); // 2025-03-12 00:00:00 UTC

    @BeforeEach
    void setUp() {
        statisticRepository.deleteAll();
    }

    @Test
    void testAddSecondsPlayedCreatesNewStatisticWhenNoneExists() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(120L, "");

            Statistic saved = statisticRepository.findByDate(FIXED_DATE);
            assertNotNull(saved);
            assertEquals(120L, saved.getSecondsPlayed());
            assertEquals(FIXED_DATE, saved.getDate());
        }
    }

    @Test
    void testAddSecondsPlayedAccumulatesOnExistingStatistic() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(60L, "");
            statisticService.addSecondsPlayed(40L, "");

            Statistic saved = statisticRepository.findByDate(FIXED_DATE);
            assertNotNull(saved);
            assertEquals(100L, saved.getSecondsPlayed());
        }
    }

    @Test
    void testAddSecondsPlayedWithZeroSeconds() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(0L, "");

            Statistic saved = statisticRepository.findByDate(FIXED_DATE);
            assertNotNull(saved);
            assertEquals(0L, saved.getSecondsPlayed());
        }
    }

    @Test
    void testGetTodayStatisticReturnsStatisticWhenExists() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(300L, "");

            Statistic result = statisticService.getTodayStatistic();

            assertNotNull(result);
            assertEquals(300L, result.getSecondsPlayed());
            assertEquals(FIXED_DATE, result.getDate());
        }
    }

    @Test
    void testGetTodayStatisticThrowsWhenNotFound() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            assertThrows(TodayStatisticNotFoundError.class, () -> statisticService.getTodayStatistic());
        }
    }

    // ── feedStats ────────────────────────────────────────────────────────────

    @Test
    void testAddSecondsPlayedStoresFeedStats() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(120L, "FeedA");

            Statistic saved = statisticRepository.findByDate(FIXED_DATE);
            assertNotNull(saved);
            assertTrue(saved.getFeedStats().stream().anyMatch(f -> f.getFeedName().equals("FeedA") && f.getSecondsPlayed() == 120L));
        }
    }

    @Test
    void testAddSecondsPlayedAccumulatesFeedStatsAcrossMultipleCalls() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(60L, "FeedA");
            statisticService.addSecondsPlayed(40L, "FeedA");
            statisticService.addSecondsPlayed(30L, "FeedB");

            Statistic saved = statisticRepository.findByDate(FIXED_DATE);
            assertNotNull(saved);
            assertTrue(saved.getFeedStats().stream().anyMatch(f -> f.getFeedName().equals("FeedA") && f.getSecondsPlayed() == 100L));
            assertTrue(saved.getFeedStats().stream().anyMatch(f -> f.getFeedName().equals("FeedB") && f.getSecondsPlayed() == 30L));
        }
    }

    @Test
    void testAddSecondsPlayedSkipsFeedEntryWhenFeedNameIsBlank() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils mockTimeUtils = mock(TimeUtils.class);
            mockedStatic.when(TimeUtils::getInstance).thenReturn(mockTimeUtils);
            when(mockTimeUtils.getCurrentDate()).thenReturn(FIXED_DATE);

            statisticService.addSecondsPlayed(60L, "");

            Statistic saved = statisticRepository.findByDate(FIXED_DATE);
            assertNotNull(saved);
            assertTrue(saved.getFeedStats() == null || saved.getFeedStats().isEmpty());
        }
    }

    @Test
    void testGetSummaryWeekDay_IncludesFeedStatSummaryForFeedA() {
        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils spyUtils = spy(new TimeUtils());
            mockedStatic.when(TimeUtils::getInstance).thenReturn(spyUtils);
            doReturn(FIXED_DATE).when(spyUtils).getCurrentDate();
            doReturn(new Date(0)).when(spyUtils).getStartOfWeek();

            statisticService.addSecondsPlayed(1800L, "FeedA");

            List<WeekDaySummary> result = statisticService.getSummaryWeekDay();

            WeekDaySummary wednesday = result.stream()
                    .filter(s -> s.name().equals("Wednesday"))
                    .findFirst().orElseThrow();
            assertTrue(wednesday.feedStats().stream()
                    .anyMatch(f -> f.getFeedName().equals("FeedA") && f.getSecondsPlayed() == 1800L));
        }
    }

    // ── getSummaryWeekDay ────────────────────────────────────────────────────

    @Test
    void testGetSummaryWeekDayReturnsAllDaysInitialisedToZeroWhenNoData() {
        List<WeekDaySummary> result = statisticService.getSummaryWeekDay();

        assertEquals(7, result.size());
        assertEquals(Arrays.asList(ConstantText.DAYS_OF_WEEK),
                result.stream().map(WeekDaySummary::name).toList());
        assertTrue(result.stream().allMatch(s -> s.totalSeconds() == 0));
        assertTrue(result.stream().allMatch(s -> s.percentage() == 0.0));
    }

    @Test
    void testGetSummaryWeekDayAccumulatesSecondsForCorrectDay() {
        String expectedWeekDay = "Wednesday";

        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils spyUtils = spy(new TimeUtils());
            mockedStatic.when(TimeUtils::getInstance).thenReturn(spyUtils);
            doReturn(new Date(0)).when(spyUtils).getStartOfWeek();
            doReturn(new Date(Long.MAX_VALUE / 2)).when(spyUtils).getCurrentDate();

            statisticRepository.save(buildDemoStatistic(500L));

            List<WeekDaySummary> result = statisticService.getSummaryWeekDay();

            WeekDaySummary match = result.stream()
                    .filter(s -> s.name().equals(expectedWeekDay))
                    .findFirst()
                    .orElseThrow();
            assertEquals(500L, match.totalSeconds());
            assertEquals(100.0, match.percentage());

            result.stream()
                    .filter(s -> !s.name().equals(expectedWeekDay))
                    .forEach(s -> assertEquals(0L, s.totalSeconds(),
                            "Expected 0 seconds for day: " + s.name()));
        }
    }

    @Test
    void testGetSummaryWeekDayAccumulatesAcrossMultipleStatisticsOnSameDay() {
        String expectedWeekDay = "Wednesday";

        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils spyUtils = spy(new TimeUtils());
            mockedStatic.when(TimeUtils::getInstance).thenReturn(spyUtils);
            doReturn(new Date(0)).when(spyUtils).getStartOfWeek();
            doReturn(new Date(Long.MAX_VALUE / 2)).when(spyUtils).getCurrentDate();

            for (long offset = 0; offset < 2; offset++) {
                Date date = new Date(FIXED_DATE.getTime() + offset * 1000);
                Statistic stat = new Statistic();
                stat.setId(FIXED_DATE.getTime() + offset);
                stat.setDate(date);
                stat.addSecondsPlayed(200L, "");
                statisticRepository.save(stat);
            }

            List<WeekDaySummary> result = statisticService.getSummaryWeekDay();

            WeekDaySummary wednesday = result.stream()
                    .filter(s -> s.name().equals(expectedWeekDay))
                    .findFirst().orElseThrow();
            assertEquals(400L, wednesday.totalSeconds());
            assertEquals(100.0, wednesday.percentage());
        }
    }

    @Test
    void testGetSummaryWeekDay_PercentageProportionalAcrossMultipleDays() {
        // FIXED_DATE = Wednesday, +1 day = Thursday
        long oneDayMs = 24 * 60 * 60 * 1000L;
        Date thursday = new Date(FIXED_DATE.getTime() + oneDayMs);

        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils spyUtils = spy(new TimeUtils());
            mockedStatic.when(TimeUtils::getInstance).thenReturn(spyUtils);
            doReturn(new Date(0)).when(spyUtils).getStartOfWeek();
            doReturn(new Date(Long.MAX_VALUE / 2)).when(spyUtils).getCurrentDate();

            statisticRepository.save(buildDemoStatistic(600L));     // Wednesday: 600s → 60%

            Statistic thursdayStat = new Statistic();
            thursdayStat.setId(thursday.getTime());
            thursdayStat.setDate(thursday);
            thursdayStat.addSecondsPlayed(400L, "");                    // Thursday:  400s → 40%
            statisticRepository.save(thursdayStat);

            List<WeekDaySummary> result = statisticService.getSummaryWeekDay();

            WeekDaySummary wed = result.stream().filter(s -> s.name().equals("Wednesday")).findFirst().orElseThrow();
            WeekDaySummary thu = result.stream().filter(s -> s.name().equals("Thursday")).findFirst().orElseThrow();

            assertEquals(600L, wed.totalSeconds());
            assertEquals(60.0, wed.percentage());
            assertEquals(400L, thu.totalSeconds());
            assertEquals(40.0, thu.percentage());
        }
    }

    // ── getWeekSummary(DateRange) ────────────────────────────────────────────

    @Test
    void testGetWeekSummary_WithExplicitRange_AccumulatesCorrectSeconds() {
        statisticRepository.save(buildDemoStatistic(500L));

        DateRange range = new DateRange(new Date(0), new Date(Long.MAX_VALUE / 2));
        List<WeekDaySummary> result = statisticService.getWeekSummary(range);

        WeekDaySummary wednesday = result.stream()
                .filter(s -> s.name().equals("Wednesday"))
                .findFirst().orElseThrow();
        assertEquals(500L, wednesday.totalSeconds());
        assertEquals(100.0, wednesday.percentage());
    }

    @Test
    void testGetWeekSummary_WithRangeExcludingStatistic_ReturnsZeroTotals() {
        statisticRepository.save(buildDemoStatistic(500L));

        DateRange range = new DateRange(new Date(0), new Date(1000));
        List<WeekDaySummary> result = statisticService.getWeekSummary(range);

        assertTrue(result.stream().allMatch(s -> s.totalSeconds() == 0));
    }

    // ── getSummaryMonth ──────────────────────────────────────────────────────

    @Test
    void testGetSummaryMonthReturnsAllMonthsInitialisedToZeroWhenNoData() {
        List<MonthSummary> result = statisticService.getSummaryMonth();

        assertEquals(ConstantText.MONTH_NAMES.length, result.size());
        assertEquals(Arrays.asList(ConstantText.MONTH_NAMES),
                result.stream().map(MonthSummary::name).toList());
        assertTrue(result.stream().allMatch(s -> s.totalSeconds() == 0));
        assertTrue(result.stream().allMatch(s -> s.weekDayBreakdown().size() == ConstantText.DAYS_OF_WEEK.length));
    }

    @Test
    void testGetSummaryMonthAccumulatesSecondsForCorrectMonth() {
        String expectedMonth = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        String expectedDay   = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);

        statisticRepository.save(buildDemoStatistic(3600L));

        List<MonthSummary> result = statisticService.getSummaryMonth();

        MonthSummary match = result.stream()
                .filter(s -> s.name().equals(expectedMonth))
                .findFirst()
                .orElseThrow();
        assertEquals(3600L, match.totalSeconds());
        assertEquals(3600L, match.weekDayBreakdown().get(expectedDay).totalSeconds());
        assertEquals(100.0, match.weekDayBreakdown().get(expectedDay).percentage());

        result.stream()
                .filter(s -> !s.name().equals(expectedMonth))
                .forEach(s -> assertEquals(0L, s.totalSeconds(),
                        "Expected 0 seconds for month: " + s.name()));
    }

    @Test
    void testGetSummaryMonth_OtherDaysInBreakdownAreZeroWhenMonthHasSingleDay() {
        String expectedMonth = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        String expectedDay   = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);

        statisticRepository.save(buildDemoStatistic(3600L));

        List<MonthSummary> result = statisticService.getSummaryMonth();
        MonthSummary match = result.stream()
                .filter(s -> s.name().equals(expectedMonth))
                .findFirst().orElseThrow();

        match.weekDayBreakdown().forEach((day, breakdown) -> {
            if (!day.equals(expectedDay)) {
                assertEquals(0L, breakdown.totalSeconds(), "Expected 0 totalSeconds for: " + day);
                assertEquals(0.0, breakdown.percentage(), "Expected 0.0 percentage for: " + day);
            }
        });
    }

    @Test
    void testGetSummaryMonth_BreakdownSplitAcrossMultipleDaysInSameMonth() {
        // FIXED_DATE = Wednesday 2025-03-12, +1 day = Thursday 2025-03-13 (still March)
        long oneDayMs = 24 * 60 * 60 * 1000L;
        Date thursday = new Date(FIXED_DATE.getTime() + oneDayMs);

        statisticRepository.save(buildDemoStatistic(600L));

        Statistic thursdayStat = new Statistic();
        thursdayStat.setId(thursday.getTime());
        thursdayStat.setDate(thursday);
        thursdayStat.addSecondsPlayed(400L, "");
        statisticRepository.save(thursdayStat);

        String expectedMonth = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        List<MonthSummary> result = statisticService.getSummaryMonth();
        MonthSummary march = result.stream()
                .filter(s -> s.name().equals(expectedMonth))
                .findFirst().orElseThrow();

        assertEquals(1000L, march.totalSeconds());
        assertEquals(600L, march.weekDayBreakdown().get("Wednesday").totalSeconds());
        assertEquals(60.0, march.weekDayBreakdown().get("Wednesday").percentage());
        assertEquals(400L, march.weekDayBreakdown().get("Thursday").totalSeconds());
        assertEquals(40.0, march.weekDayBreakdown().get("Thursday").percentage());
    }

    @Test
    void testGetSummaryMonth_BreakdownIsolatedBetweenMonths() {
        // FIXED_DATE = Wednesday 2025-03-12 (March); +35 days = Wednesday 2025-04-16 (April)
        long thirtyFiveDaysMs = 35L * 24 * 60 * 60 * 1000L;
        Date aprilDate = new Date(FIXED_DATE.getTime() + thirtyFiveDaysMs);

        statisticRepository.save(buildDemoStatistic(1800L));    // March

        Statistic aprilStat = new Statistic();
        aprilStat.setId(aprilDate.getTime());
        aprilStat.setDate(aprilDate);
        aprilStat.addSecondsPlayed(3600L, "");                      // April
        statisticRepository.save(aprilStat);

        String marchName = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        String aprilName = TimeUtils.getInstance().dateToMonth(aprilDate);

        List<MonthSummary> result = statisticService.getSummaryMonth();
        MonthSummary march = result.stream().filter(s -> s.name().equals(marchName)).findFirst().orElseThrow();
        MonthSummary april = result.stream().filter(s -> s.name().equals(aprilName)).findFirst().orElseThrow();

        assertEquals(1800L, march.totalSeconds());
        assertEquals(1800L, march.weekDayBreakdown().values().stream().mapToLong(WeekDayBreakdown::totalSeconds).sum());
        assertEquals(3600L, april.totalSeconds());
        assertEquals(3600L, april.weekDayBreakdown().values().stream().mapToLong(WeekDayBreakdown::totalSeconds).sum());
    }

    // ── getStatisticsByDateRange ─────────────────────────────────────────────

    @Test
    void testGetStatisticsByDateRangeReturnsMatchingStatistics() {
        Statistic statistic = buildDemoStatistic(120L);
        statisticRepository.save(statistic);

        Date from = new Date(FIXED_DATE.getTime() - 1000);
        Date to   = new Date(FIXED_DATE.getTime() + 1000);
        List<Statistic> result = statisticService.getStatisticsByDateRange(new DateRange(from, to));

        assertEquals(1, result.size());
        assertEquals(FIXED_DATE, result.getFirst().getDate());
        assertEquals(120L, result.getFirst().getSecondsPlayed());
    }

    @Test
    void testGetStatisticsByDateRangeReturnsMatchingStatisticsIncludeStartDate() {
        Statistic statistic = buildDemoStatistic(120L);
        statisticRepository.save(statistic);

        Date from = new Date(FIXED_DATE.getTime());
        Date to   = new Date(FIXED_DATE.getTime() + 1000);
        List<Statistic> result = statisticService.getStatisticsByDateRange(new DateRange(from, to));

        assertEquals(1, result.size());
        assertEquals(FIXED_DATE, result.getFirst().getDate());
        assertEquals(120L, result.getFirst().getSecondsPlayed());
    }

    private static Statistic buildDemoStatistic(Long secondsPlayed) {
        Statistic statistic = new Statistic();
        statistic.setId(FIXED_DATE.getTime());
        statistic.setDate(FIXED_DATE);
        statistic.addSecondsPlayed(secondsPlayed, "");
        return statistic;
    }

    @Test
    void testGetStatisticsByDateRangeReturnsMatchingStatisticsIncludeEndDate() {
        Statistic statistic = buildDemoStatistic(120L);
        statisticRepository.save(statistic);

        Date from = new Date(FIXED_DATE.getTime() - 1000);
        Date to   = new Date(FIXED_DATE.getTime());
        List<Statistic> result = statisticService.getStatisticsByDateRange(new DateRange(from, to));

        assertEquals(1, result.size());
        assertEquals(FIXED_DATE, result.getFirst().getDate());
        assertEquals(120L, result.getFirst().getSecondsPlayed());
    }

    @Test
    void testGetStatisticsByDateRangeReturnsEmptyWhenNoneInRange() {
        Statistic statistic = buildDemoStatistic(120L);
        statisticRepository.save(statistic);

        Date from = new Date(FIXED_DATE.getTime() + 1_000_000L);
        Date to   = new Date(FIXED_DATE.getTime() + 2_000_000L);
        List<Statistic> result = statisticService.getStatisticsByDateRange(new DateRange(from, to));

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetStatisticsByDateRangeReturnsEmptyWhenRepositoryIsEmpty() {
        Date from = new Date(0);
        Date to   = new Date(Long.MAX_VALUE);
        List<Statistic> result = statisticService.getStatisticsByDateRange(new DateRange(from, to));

        assertTrue(result.isEmpty());
    }
}

