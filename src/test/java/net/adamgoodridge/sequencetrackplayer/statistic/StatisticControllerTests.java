package net.adamgoodridge.sequencetrackplayer.statistic;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.utils.TimeUtils;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.*;

import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StatisticControllerTests extends AbstractSpringBootTest {

    private static final String BASE_URL        = "/api/v1/statistic";
    private static final String TODAY_URL        = BASE_URL + "/today";
    private static final String WEEK_SUMMARY_URL = BASE_URL + "/summary/week";
    private static final String MONTH_SUMMARY_URL= BASE_URL + "/summary/month";

    // 2025-03-12 00:00:00 UTC — same anchor used across the test suite
    private static final Date FIXED_DATE = new Date(1741737600000L);

    @Autowired private MockMvc mockMvc;
    @Autowired private StatisticRepository statisticRepository;

    @BeforeEach
    void setUp() {
        statisticRepository.deleteAll();
    }

    // ── GET /api/v1/statistic/ ───────────────────────────────────────────────

    @Test
    void getStatistics_NoParams_ReturnsEmptyArrayWhenNoData() throws Exception {
        mockMvc.perform(get(BASE_URL + "/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getStatistics_NoParams_ReturnsStatisticsWithinDefaultWindow() throws Exception {
        saveStatistic(FIXED_DATE, 60L);

        mockMvc.perform(get(BASE_URL + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getStatistics_WithDaysParam_ReturnsStatisticsWithinWindow() throws Exception {
        saveStatistic(FIXED_DATE, 120L);

        // use a large days window so FIXED_DATE is always within range
        mockMvc.perform(get(BASE_URL + "/").param("days", "3650"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].secondsPlayed").value(120));
    }

    @Test
    void getStatistics_WithFromAndToParams_ReturnsMatchingStatistics() throws Exception {
        saveStatistic(FIXED_DATE, 180L);

        mockMvc.perform(get(BASE_URL + "/")
                        .param("fromDate", "2025-03-11")
                        .param("toDate",   "2025-03-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value("2025-03-12"))
				.andExpect(jsonPath("$[0].dayOfWeek").value("Wednesday"))
                .andExpect(jsonPath("$[0].secondsPlayed").value(180));
    }

    @Test
    void getStatistics_WithFromAndToParams_ReturnsEmptyWhenOutsideRange() throws Exception {
        saveStatistic(FIXED_DATE, 180L);

        mockMvc.perform(get(BASE_URL + "/")
                        .param("fromDate", "2020-01-01")
                        .param("toDate",   "2020-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getStatistics_WithToMissing_ReturnDateFromThatDate() throws Exception {
        saveStatistic(FIXED_DATE, 180L);

        mockMvc.perform(get(BASE_URL + "/")
                .param("fromDate", "2025-03-01"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].date").value("2025-03-12"))
                .andExpect(jsonPath("$[0].dayOfWeek").value("Wednesday"))
                .andExpect(jsonPath("$[0].secondsPlayed").value(180));
    }

    @Test
    void getStatistics_DaysAndFromTogether_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/")
                        .param("days",     "7")
                        .param("fromDate", "2025-03-01"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getStatistics_ResponseContainsExpectedFields() throws Exception {
        saveStatistic(FIXED_DATE, 3600L);
        String expectedDay  = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);

        mockMvc.perform(get(BASE_URL + "/")
                        .param("fromDate", "2025-03-11")
                        .param("toDate",   "2025-03-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2025-03-12"))
                .andExpect(jsonPath("$[0].dayOfWeek").value(expectedDay))
                .andExpect(jsonPath("$[0].secondsPlayed").value(3600))
                .andExpect(jsonPath("$[0].feedStats").isArray());
    }

    @Test
    void getStatistics_ResponseContainsFeedStatsForEachFeed() throws Exception {
        Statistic s = new Statistic();
        s.setId(FIXED_DATE.getTime());
        s.setDate(FIXED_DATE);
        s.addSecondsPlayed(1800L, "FeedA");
        s.addSecondsPlayed(1800L, "FeedB");
        statisticRepository.save(s);

        mockMvc.perform(get(BASE_URL + "/")
                        .param("fromDate", "2025-03-11")
                        .param("toDate",   "2025-03-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].feedStats", hasSize(2)))
                .andExpect(jsonPath("$[0].feedStats[0].feedName").exists())
                .andExpect(jsonPath("$[0].feedStats[0].secondsPlayed").exists());
    }

    @Test
    void getWeekSummary_ResponseContainsFeedStatsField() throws Exception {
        mockMvc.perform(get(WEEK_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].feedStats").exists());
    }

    @Test
    void getWeekSummary_ResponseContainsFeedStatsArrayEntries() throws Exception {
        Statistic s = new Statistic();
        s.setId(FIXED_DATE.getTime());
        s.setDate(FIXED_DATE);
        s.addSecondsPlayed(1800L, "FeedA");
        s.addSecondsPlayed(600L, "FeedB");
        statisticRepository.save(s);

        String expectedDay = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);
        mockMvc.perform(get(WEEK_SUMMARY_URL)
                        .param("fromDate", "2025-03-11")
                        .param("toDate",   "2025-03-13"))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].feedStats").exists())
            .andExpect(content().string(containsString("\"feedName\":\"FeedA\",\"secondsPlayed\":1800")))
            .andExpect(content().string(containsString("\"feedName\":\"FeedB\",\"secondsPlayed\":600")));
    }

    @Test
    void getMonthSummary_ResponseContainsFeedStatsField() throws Exception {
        mockMvc.perform(get(MONTH_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].feedStats").exists());
    }

        @Test
        void getMonthSummary_WeekDayBreakdownContainsFeedStatsArrayEntries() throws Exception {
        Statistic s = new Statistic();
        s.setId(FIXED_DATE.getTime());
        s.setDate(FIXED_DATE);
        s.addSecondsPlayed(1200L, "FeedA");
        s.addSecondsPlayed(300L, "FeedB");
        statisticRepository.save(s);

        String expectedMonth = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        String expectedDay = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);

        mockMvc.perform(get(MONTH_SUMMARY_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name == '" + expectedMonth + "')].weekDayBreakdown."
                + expectedDay + ".feedStats").exists())
            .andExpect(content().string(containsString("\"weekDayBreakdown\"")))
            .andExpect(content().string(containsString("\"feedName\":\"FeedA\",\"secondsPlayed\":1200")))
            .andExpect(content().string(containsString("\"feedName\":\"FeedB\",\"secondsPlayed\":300")));
        }

    // ]GET /api/v1/statistic/today

    @Test
    void getTodayStatistic_Returns404WhenNothingPlayedToday() throws Exception {
        mockMvc.perform(get(TODAY_URL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Today's statistic not found as nothing has been played today"));
    }

    @Test
    void getTodayStatistic_ReturnsStatisticWhenTodayExists() throws Exception {
        Date today = TimeUtils.getInstance().getCurrentDate();
        saveStatistic(today, 900L);

        mockMvc.perform(get(TODAY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secondsPlayed").value(900));
    }

    // ── GET /api/v1/statistic/summary/week ──────────────────────────────────

    @Test
    void getWeekSummary_ReturnsAllDaysWithZeroSecondsWhenNoData() throws Exception {
        mockMvc.perform(get(WEEK_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(ConstantText.DAYS_OF_WEEK.length)))
                .andExpect(jsonPath("$[*].totalSeconds", everyItem(is(0))))
                .andExpect(jsonPath("$[*].percentage", everyItem(is(0.0))));
    }

    @Test
    void getWeekSummary_AccumulatesSecondsForCorrectDay() throws Exception {
        String expectedDay = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);
        saveStatistic(FIXED_DATE, 1800L);

        try (MockedStatic<TimeUtils> mockedStatic = mockStatic(TimeUtils.class)) {
            TimeUtils spyUtils = spy(new TimeUtils());
            mockedStatic.when(TimeUtils::getInstance).thenReturn(spyUtils);
            doReturn(new Date(0)).when(spyUtils).getStartOfWeek();
            doReturn(new Date(Long.MAX_VALUE / 2)).when(spyUtils).getCurrentDate();

            mockMvc.perform(get(WEEK_SUMMARY_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].totalSeconds",
                            contains(1800)))
                    .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].feedStats[0].secondsPlayed",
                            contains(1800)))
                    .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].feedStats[0].feedName",
                            hasItem("test")))

                    .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].percentage",
                            contains(100.0)));
        }
    }

    @Test
    void getWeekSummary_WithExplicitDateRange_ReturnsDataInRange() throws Exception {
        String expectedDay = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);
        saveStatistic(FIXED_DATE, 1800L);

        mockMvc.perform(get(WEEK_SUMMARY_URL)
                        .param("fromDate", "2025-03-11")
                        .param("toDate",   "2025-03-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].totalSeconds",
                        contains(1800)))
                .andExpect(jsonPath("$", hasSize(3) ))
                .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].feedStats",
                        hasSize(1)))
                .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].feedStats[0].secondsPlayed",
                        contains(1800)))
                .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].feedStats[0].feedName",
                        hasItem("test")))
                .andExpect(jsonPath("$[?(@.name == '" + expectedDay + "')].percentage",
                        contains(100.0)));
    }

    @Test
    void getWeekSummary_WithExplicitDateRangeExcludingStatistic_ReturnsZeroTotals() throws Exception {
        saveStatistic(FIXED_DATE, 1800L);

        mockMvc.perform(get(WEEK_SUMMARY_URL)
                        .param("fromDate", "2020-01-01")
                        .param("toDate",   "2020-12-31"))
                .andExpect(jsonPath("$", hasSize(7) ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].totalSeconds", everyItem(is(0))));
    }

    @Test
    void getWeekSummary_ResponseContainsNameTotalSecondsAndPercentageFields() throws Exception {
        mockMvc.perform(get(WEEK_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].totalSeconds").exists())
                .andExpect(jsonPath("$[0].percentage").exists());
    }

    @Test
    void getMonthSummary_ReturnsAllMonthsWithZeroSecondsWhenNoData() throws Exception {
        mockMvc.perform(get(MONTH_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(ConstantText.MONTH_NAMES.length)))
                .andExpect(jsonPath("$[*].totalSeconds", everyItem(is(0))))
                .andExpect(jsonPath("$[0].weekDayBreakdown").exists());
    }

    @Test
    void getMonthSummary_AccumulatesSecondsForCorrectMonth() throws Exception {
        String expectedMonth = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        saveStatistic(FIXED_DATE, 7200L);

        mockMvc.perform(get(MONTH_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == '" + expectedMonth + "')].totalSeconds",
                        contains(7200)));
    }

    @Test
    void getMonthSummary_WeekDayBreakdownContainsCorrectSecondsAndPercentage() throws Exception {
        String expectedMonth = TimeUtils.getInstance().dateToMonth(FIXED_DATE);
        String expectedDay   = TimeUtils.getInstance().dateToWeekDay(FIXED_DATE);
        saveStatistic(FIXED_DATE, 7200L);

        mockMvc.perform(get(MONTH_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(12) ))
                .andExpect(jsonPath("$[?(@.name == '" + expectedMonth + "')].weekDayBreakdown."
                        + expectedDay + ".totalSeconds", contains(7200)))
                .andExpect(jsonPath("$[?(@.name == '" + expectedMonth + "')].weekDayBreakdown."
                        + expectedDay + ".percentage", contains(100.0)));
    }

    @Test
    void getMonthSummary_ResponseContainsNameTotalSecondsAndWeekDayBreakdownFields() throws Exception {
        mockMvc.perform(get(MONTH_SUMMARY_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].totalSeconds").exists())
                .andExpect(jsonPath("$[0].weekDayBreakdown").exists());
    }


    private void saveStatistic(Date date, long seconds) {
        Statistic s = new Statistic();
        s.setId(date.getTime());
        s.setDate(date);
        s.addSecondsPlayed(seconds, "test");
        statisticRepository.save(s);
    }
}
