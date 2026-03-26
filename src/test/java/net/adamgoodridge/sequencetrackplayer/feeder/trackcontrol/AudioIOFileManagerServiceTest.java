package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import static org.mockito.ArgumentMatchers.anyInt;

import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AudioIOFileManagerServiceTest extends AbstractSpringBootTest {
	private AudioIOFileManagerService audioIOFileManagerService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private SettingRepository settingRepository;
	@Autowired
	private RandomNumberGenerator randomNumberGenerator;

	@BeforeEach
	void setUp() throws IOException {
		// Initialize components using AudioFeederFactory
		LoadClassDef.initializeComponents();

		MockitoAnnotations.openMocks(this);
		// Create AudioIOFileManagerService with the mocked SettingService
		audioIOFileManagerService = new AudioIOFileManagerService(settingService, randomNumberGenerator);
		new SettingRepositoryMock().fillWithMockData(settingRepository);

	}

	@Test
	void testBasicFunctionality() {
		// Basic test to ensure the class is properly set up
		assertNotNull(settingRepository);
	}
	void setSettingsRepository(PreferredRandomSettings preferredRandomSettings) {
		Setting mockDayOfWeek = new Setting("day_of_week", preferredRandomSettings.getDay());
		Setting mockHourOfDay = new Setting("hour_of_day", String.valueOf(preferredRandomSettings.getTime()));
		settingRepository.save(mockDayOfWeek);
		settingRepository.save(mockHourOfDay);

	}

	private static Stream<Arguments> randomStrategyTimePreferenceParameters() {
		// provider must be a static no-arg method for @MethodSource
		Integer[] defaultRandomIndexes = new Integer[]{0, 0, 3, 3};
		return Stream.of(
				Arguments.of("RandomStrategy_WithNoPreference_ShouldReturnTrackAtRandom",
						new PreferredRandomSettings.Builder().build(), defaultRandomIndexes,
						"FEEDB_AUDIOFILE_2022-06-21_Tuesday_17-30-00.mp3")
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("randomStrategyTimePreferenceParameters")
	void RandomStrategy_WithTimePreference_ShouldReturnTrackAtSpecifiedTime(String ignore,
																			PreferredRandomSettings preferredRandomSettings, Integer[] randomIndexes, String expectedFileName) {
		// Given: Time preference set to specified time and FeedB feed requested
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"FeedB"});
					put("/mnt/path/FeedB", new String[]{"2022"});
					put("/mnt/path/FeedB/2022", new String[]{"2022-06_June"});
					put("/mnt/path/FeedB/2022/2022-06_June", new String[]{"2022-06-21_Tuesday"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-21_Tuesday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-21_Tuesday_17-30-00.mp3"});
				}})) {
			setSettingsRepository(preferredRandomSettings);
			RandomNumberGenerator randomNumberGeneratorSpy = Mockito.spy(randomNumberGenerator);
			when(randomNumberGeneratorSpy.getRandomNumber(anyInt())).thenReturn(0);
			// Create a new service instance with the spy
			AudioIOFileManagerService serviceWithSpy = new AudioIOFileManagerService(settingService, randomNumberGeneratorSpy);
			FeedRequest feedRequest = FeedRequest.builder()
					.name("FeedB")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			AudioIOFileManager result = serviceWithSpy.generateFeed(feedRequest);


			// Then
			verify(randomNumberGeneratorSpy, atLeastOnce()).getRandomNumber(anyInt());
			assertNotNull(result);
			assertEquals(expectedFileName, result.getFile().getFileName());
			// Verify the randomNumberGenerator was called
		}
	}
	@Test
	void RandomStrategy_GetRandomTrackEachTime() {
		// Given: Time preference set to specified time and FeedB feed requested
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"FeedB"});
					put("/mnt/path/FeedB", new String[]{"2022"});
					put("/mnt/path/FeedB/2022", new String[]{"2022-06_June"});
					put("/mnt/path/FeedB/2022/2022-06_June", new String[]{"2022-06-05_Sunday", "2022-06-06_Monday", "2022-06-21_Tuesday"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-05_Sunday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-05_Sunday_09-00-00.mp3", "FEEDB_AUDIOFILE_2022-06-05_Sunday_12-30-00.mp3"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-06_Monday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-06_Monday_09-15-00.mp3", "FEEDB_AUDIOFILE_2022-06-06_Monday_17-30-00.mp3"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-21_Tuesday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-21_Tuesday_09-15-00.mp3", "FEEDB_AUDIOFILE_2022-06-21_Tuesday_17-30-00.mp3"});
				}})) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.build();
			setSettingsRepository(settings);
			FeedRequest feedRequest = FeedRequest.builder()
					.name("FeedB")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			AudioIOFileManager result = audioIOFileManagerService.generateFeed(feedRequest);
			String previousFileName = result.getFile().getFileName();
			int i = 0;
			boolean gerneratedDifferentFile = false;
			while (i < 100) {
				result = audioIOFileManagerService.generateFeed(feedRequest);
				if(!previousFileName.equals(result.getFile().getFileName())) {
					gerneratedDifferentFile = true;
					break;
				}
				previousFileName = result.getFile().getFileName();
				i++;
			}
			// Then
			assertTrue(gerneratedDifferentFile, "The file name has changed so it is random");
		}
	}


	@Test
	void RandomStrategy_WithDayPreferenceNoExactMatchFound_ShouldThrowError() {
		// Given: Day preference set to "Tuesday" for a path that doesn't contain Tuesday files
		// Use a minimal mocked filesystem for this test: the month folder exists but has no Tuesday folder
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"FeedB"});
					put("/mnt/path/FeedB", new String[]{"2022"});
					put("/mnt/path/FeedB/2022", new String[]{"2022-06_June"});
					put("/mnt/path/FeedB/2022/2022-06_June", new String[]{"2022-06-05_Sunday", "2022-06-06_Monday", "2022-06-12_Sunday", "2022-06-22_Wednesday", "2022-06-23_Thursday"});
				}})) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.day("Tuesday")
					.build();
			setSettingsRepository(settings);

			FeedRequest feedRequest = FeedRequest.builder()
					.name("FeedB")
					.path("FeedB/2022/2022-06_June")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			GetFeedError error = assertThrows(GetFeedError.class, () -> audioIOFileManagerService.generateFeed(feedRequest));

			// Then: Exception with expected error message should be thrown
			assertTrue(error.getMessage().contains("After multiple attempts"));
		}
	}

	@Test
	void RandomStrategy_ShouldThrowException_WhenNoFilesFound() {
		// Given: A request for a non-existent feed
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.build();
			setSettingsRepository(settings);

			FeedRequest feedRequest = FeedRequest.builder()
					.name("nonexistent")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			GetFeedError exception = assertThrows(GetFeedError.class, () -> {
				audioIOFileManagerService.generateFeed(feedRequest);
			});

			// Then: Exception with expected error message should be thrown
			String expectedPath = "After multiple attempts, a random track cannot be gotten! " +
					"The last error was: Cannot find random track for /mnt/path/nonexistent, Reason: path was empty: /mnt/path/nonexistent";
			assertTrue(exception.getMessage().contains(expectedPath));
		}
	}

	@Test
	void RandomStrategy_ShouldThrowException_WhenSubFilesNotFound() {
		// Given: A request with an empty path
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder().build();
			setSettingsRepository(settings);

			FeedRequest feedRequest = FeedRequest.builder().build();

			// When: GenerateFeed is initialized and process is called
			GetFeedError exception = assertThrows(GetFeedError.class, () -> {
				audioIOFileManagerService.generateFeed(feedRequest);
			});

			// Then: Exception with expected error message should be thrown
			String expectedMessage = "After multiple attempts, a random track cannot be gotten!";
			assertTrue(exception.getMessage().contains(expectedMessage));
		}
	}

	@Test
	void RandomStrategy_WithDayAndTimePreference_ShouldReturnTrackOnSpecifiedDayAndTime()  {
		// Given: Day preference set to "Monday" and time preference set to 12
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"FeedB"});
					put("/mnt/path/FeedB", new String[]{"2022"});
					put("/mnt/path/FeedB/2022", new String[]{"2022-06_June"});
					put("/mnt/path/FeedB/2022/2022-06_June", new String[]{"2022-06-06_Monday"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-06_Monday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-06_Monday_12-30-00.mp3"});
				}})) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.day("Monday")
					.time(12)
					.build();
			setSettingsRepository(settings);

			FeedRequest feedRequest = FeedRequest.builder()
					.name("FeedB")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			AudioIOFileManager result = audioIOFileManagerService.generateFeed(feedRequest);

			// Then: A track from Monday at 12:xx should be returned
			assertNotNull(result);
			assertTrue(result.getFile().getFileName().contains("Monday"));
			assertTrue(result.getFile().getFileName().contains("12-"));
		}
	}

	@Test
	void RandomStrategy_WithTimePreference_ShouldFindExactTimeMatch() throws ExecutionException, InterruptedException {
		// Given: Time preference set to 12
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"FeedB"});
					put("/mnt/path/FeedB", new String[]{"2022"});
					put("/mnt/path/FeedB/2022", new String[]{"2022-06_June"});
					put("/mnt/path/FeedB/2022/2022-06_June", new String[]{"2022-06-06_Monday"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-06_Monday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-06_Monday_12-30-00.mp3"});
				}})) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.time(12)
					.build();
			setSettingsRepository(settings);

			FeedRequest feedRequest = FeedRequest.builder()
					.name("FeedB")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			AudioIOFileManager result = audioIOFileManagerService.generateFeed(feedRequest);

			// Then: A track with the requested time (12:xx) should be returned
			assertNotNull(result);
			assertTrue(result.getFile().getFileName().contains("12-"));
		}
	}


	@Test
	void PathStrategy_WithValidPath_ShouldReturnSpecificTrack() {
		// Given: A request with a specific file path
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"test"});
					put("/mnt/path/test", new String[]{"2023"});
					put("/mnt/path/test/2023", new String[]{"2023-02_February"});
					put("/mnt/path/test/2023/2023-02_February", new String[]{"2023-02-01_Wednesday"});
					put("/mnt/path/test/2023/2023-02_February/2023-02-01_Wednesday",
							new String[]{"TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3"});
				}})) {
			FeedRequest feedRequest = FeedRequest.builder()
					.path("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3")
					.build();
			// When: GenerateFeed is initialized and process is called
			AudioIOFileManager result = audioIOFileManagerService.generateFeed(feedRequest);

			// Then: The exact requested file should be returned
			assertNotNull(result);
			assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.getFile().getFileName());
		}
	}

	@Test
	void PathStrategy_WithNonExistentPath_ShouldThrowException() {
		// Given: A request with a non-existent path
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path/test/2023/nonexistent", new String[]{});
				}})) {
			FeedRequest feedRequest = FeedRequest.builder()
					.path("test/2023/nonexistent")
					.build();

			// When: GenerateFeed is initialized and process is called
			GetFeedError exception = assertThrows(GetFeedError.class, () -> {
				audioIOFileManagerService.generateFeed(feedRequest);
			});

			// Then: Exception with expected error message should be thrown
			assertTrue(exception.getMessage().contains("Cannot find"));
		}
	}


	@Test
	void RandomStrategy_WithNonExistingTimePreference_ShouldFallbackToClosestTime() throws ExecutionException, InterruptedException {
		// Given: Time preference set to 15 (which doesn't exist, so should fall back to 17)
		try (MockedConstruction<File> ignored = FileSystemMockConstruction.process(
				new HashMap<>() {{
					put("/mnt/path", new String[]{"FeedB"});
					put("/mnt/path/FeedB", new String[]{"2022"});
					put("/mnt/path/FeedB/2022", new String[]{"2022-06_June"});
					put("/mnt/path/FeedB/2022/2022-06_June", new String[]{"2022-06-21_Tuesday"});
					put("/mnt/path/FeedB/2022/2022-06_June/2022-06-21_Tuesday",
							new String[]{"FEEDB_AUDIOFILE_2022-06-21_Tuesday_17-30-00.mp3", "FEEDB_AUDIOFILE_2022-06-21_Tuesday_09-15-00.mp3"});
				}})) {
			PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
					.time(15)
					.build();
			setSettingsRepository(settings);

			FeedRequest feedRequest = FeedRequest.builder()
					.name("FeedB")
					.feedRequestType(FeedRequestType.RANDOM)
					.build();

			// When: GenerateFeed is initialized and process is called
			AudioIOFileManager result = new RetrieveAudioFeeder(feedRequest.getName(),
					new GetIndexByRandomStrategy(settings, randomNumberGenerator))
					.compute();

			// Then: A track with a fallback time (17:xx) should be returned
			assertNotNull(result);
			assertTrue(result.getFile().getFileName().contains("17-"));
		}
	}
}

