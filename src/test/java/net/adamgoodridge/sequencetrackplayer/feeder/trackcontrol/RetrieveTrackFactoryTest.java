package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.intThat;
import org.mockito.stubbing.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.bean.override.mockito.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;;

@ExtendWith(MockitoExtension.class)
class AudioIOFileManagerServiceTests extends AbstractSpringBootTest {
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
    void setSettingsRepository(PreferredRandomSettings preferredRandomSettings) {
        Setting mockDayOfWeek = new Setting("day_of_week", preferredRandomSettings.getDay());
        Setting mockHourOfDay = new Setting("hour_of_day", String.valueOf(preferredRandomSettings.getTime()));
        settingRepository.save(mockDayOfWeek);
        settingRepository.save(mockHourOfDay);

    }
//todo*
    // Define parameters for parameterized tests
    private static Stream<Arguments> randomStrategyTimePreferenceParameters() {
        return Stream.of(
            //Arguments.of(12, "*", new Integer[]{0, 0, 1, 0}, "FEEDB_AUDIOFILE_2022-06-06_Monday_12-30-00.mp3"),
            Arguments.of("RandomStrategy_WithNoPreference_ShouldReturnTrackAtRandom",
                    new PreferredRandomSettings.Builder().build(), new Integer[]{0, 0, 3, 3},
                    "FEEDB_AUDIOFILE_2022-06-21_Tuesday_17-30-00.mp3")/*,
            Arguments.of("RandomStrategy_WithDayPreference_ShouldReturnTrackOnSpecifiedDay",
                    -1, "Monday", new Integer[]{0, 0, 0, 2}, "FEEDB_AUDIOFILE_2022-06-12_Sunday_14-44-14.mp3"
                    )*/

        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("randomStrategyTimePreferenceParameters")
    void RandomStrategy_WithTimePreference_ShouldReturnTrackAtSpecifiedTime(String ignore,
            PreferredRandomSettings preferredRandomSettings, Integer[] randomIndexes, String expectedFileName) {
        // Given: Time preference set to specified time and FeedB feed requested
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            setSettingsRepository(preferredRandomSettings);
            RandomNumberGenerator randomNumberGeneratorSpy = Mockito.spy(randomNumberGenerator);
            /*
            OngoingStubbing<Integer> stub = when(randomNumberGeneratorMock.getRandomNumber(anyInt()));
            for (Integer value : randomIndexes) {
                stub = stub.thenReturn(value);
            }*/
            when(randomNumberGeneratorSpy.getRandomNumber(intThat(arg -> arg > 0))).thenReturn(35);
            int debug = randomNumberGenerator.getRandomNumber(2111);
            FeedRequest feedRequest = FeedRequest.builder()
                    .name("FeedB")
                    .feedRequestType(FeedRequestType.RANDOM)
                    .build();

            // When: GenerateFeed is initialized and process is called
            AudioIOFileManager result = audioIOFileManagerService.generateFeed(feedRequest);


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
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
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
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
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
            GetFeedError error = assertThrows(GetFeedError.class, () -> {
                audioIOFileManagerService.generateFeed(feedRequest);
            });

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
            String expectedMessage = "After multiple attempts, a random track cannot be gotten! The last error was:" +
                    " Cannot find track for /mnt/path/, Reason: path was empty: /mnt/path/";
            assertTrue(exception.getMessage().contains(expectedMessage));
        }
    }

    @Test
    void RandomStrategy_WithDayAndTimePreference_ShouldReturnTrackOnSpecifiedDayAndTime() throws ExecutionException, InterruptedException {
        // Given: Day preference set to "Monday" and time preference set to 12
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
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
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
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
    void RandomStrategy_WithNonExistingTimePreference_ShouldFallbackToClosestTime() throws ExecutionException, InterruptedException {
        // Given: Time preference set to 15 (which doesn't exist, so should fall back to 17)
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                    .time(15)
                    .build();
            setSettingsRepository(settings);

            FeedRequest feedRequest = FeedRequest.builder()
                    .name("FeedB")
                    .feedRequestType(FeedRequestType.RANDOM)
                    .build();

            // When: GenerateFeed is initialized and process is called
            AudioIOFileManager result = audioIOFileManagerService.generateFeed(feedRequest);

            // Then: A track with a fallback time (17:xx) should be returned
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().contains("17-"));
        }
    }

    @Test
    void PathStrategy_WithValidPath_ShouldReturnSpecificTrack() {
        // Given: A request with a specific file path
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            FeedRequest feedRequest = FeedRequest.builder()
                    .path("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3")
                    .build();
            Mockito.when(settingService.getPreferredRandomSettings()).thenReturn(PreferredRandomSettings.builder().build());

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
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
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
    void PathStrategy_WithMissingFile_ShouldThrowException() {
        // Given: A request with a specific file that doesn't exist
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            FeedRequest feedRequest = FeedRequest.builder()
                    .path("test/2023/2023-02_February/2023-02-01_Wednesday/nonexistent.mp3")
                    .build();
            // When: GenerateFeed is initialized and process is called
            GetFeedError exception = assertThrows(GetFeedError.class, () -> {
                audioIOFileManagerService.generateFeed(feedRequest);
            });

            // Then: Exception with expected error message should be thrown
            assertTrue(exception.getMessage().contains("Cannot find"));
        }
    }

}
