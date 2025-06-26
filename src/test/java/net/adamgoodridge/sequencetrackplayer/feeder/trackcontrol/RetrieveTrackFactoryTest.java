package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.util.concurrent.*;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class RetrieveTrackFactoryTest extends AbstractSpringBootTest {

    @BeforeEach
    void setUp() {
        // Initialize components using AudioFeederFactory
        initializeComponents();
    }

    private void initializeComponents() {
        new GetIndexByRandomStrategy(PreferredRandomSettings.builder().build());
        new PreferredRandomSettings.Builder().build();
        new net.adamgoodridge.sequencetrackplayer.filesystem.RetrieveAudioFeeder("test", new GetIndexByRandomStrategy(PreferredRandomSettings.builder().build()));
        new GetIndexByPathStrategy();
        @SuppressWarnings("unused")
        FeedRequestType type = FeedRequestType.RANDOM;
        new AudioFeederFactory(PreferredRandomSettings.builder().build(), FeedRequest.builder().build());
    }

    @Test
    void RandomStrategy_WithTimePreference_ShouldReturnTrackAtSpecifiedTime() throws ExecutionException, InterruptedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                    .time(12)
                    .build();
            AudioFeederFactory factory = new AudioFeederFactory(settings, FeedRequest.builder().build());
            AudioIOFileManager result = factory.process().getCompletableFuture().get();
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().contains("12-"));
        }
    }

    @Test
    void RandomStrategy_ShouldThrowException_WhenNoFilesFound() {
            try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
                PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                        .build();
                FeedRequest feedRequest = FeedRequest.builder()
                        .name("nonexistent")
                        .feedRequestType(FeedRequestType.RANDOM)
                        .build();
                AudioFeederFactory factory = new AudioFeederFactory(settings, feedRequest);
                AudioFeeder audioFeeder = factory.process();
                assertEquals("nonexistent", audioFeeder.getFeedName());
                assertEquals("", audioFeeder.getErrorMessage());
        };
    }

    @Test
    void RandomStrategy_ShouldThrowException_WhenSubFilesFound() {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder().build();
            AudioFeederFactory factory = new AudioFeederFactory(settings, FeedRequest.builder().build());
            GetFeedException exception = assertThrows(GetFeedException.class, () -> factory.process().getCompletableFuture().get());
            assertEquals("Cannot find random track for /mnt/path/test/nonexistent, Reason: Cannot find next folder: /mnt/path/test/nonexistent", exception.getMessage());
        }
    }

    @Test
    void RandomStrategy_WithDayPreference() throws ExecutionException, InterruptedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                    .day("Monday")
                    .build();
            FeedRequest feedRequest = FeedRequest.builder()
                    .name("FeedB")
                    .feedRequestType(FeedRequestType.RANDOM)
                    .build();
            AudioFeederFactory factory = new AudioFeederFactory(settings, feedRequest);
            AudioFeeder audioFeeder = factory.process();
            AudioIOFileManager result = audioFeeder.getCompletableFuture().get();
            assertEquals("FeedB", audioFeeder.getFeedName());
            assertNotNull(result);
            assertTrue(result.getFile().getFileName().contains("Monday"));
        }
    }

    @Test
    void RandomStrategy_WithDayPreference_ShouldReturnTrackOnSpecifiedDayAndTimeWhenTheExactTimeFound()  {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                    .day("Monday")
                    .time(12)
                    .build();
            AudioFeederFactory factory = new AudioFeederFactory(settings, FeedRequest.builder().build());
            AudioIOFileManager result = factory.process().getCompletableFuture().get();
            System.out.println("File: " + result.getFile().getFullPath());
            assertTrue(result.getFile().getFileName().contains("Monday"));
            assertTrue(result.getFile().getFileName().contains("12-"));
        } catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

    @Test
    void RandomStrategy_WithDayPreference_ShouldReturnTrackOnSpecifiedDayAndTimeWhenTheExactTimeNotFound() throws ExecutionException, InterruptedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                    .time(15)
                    .build();
            AudioFeederFactory factory = new AudioFeederFactory(settings, FeedRequest.builder().build());
            AudioIOFileManager result = factory.process().getCompletableFuture().get();
            System.out.println("File: " + result.getFile().getFullPath());
            assertTrue(result.getFile().getFileName().contains("17-"));
        }
    }

    @Test
    void WithDayPreference_ShouldReturnTrackOnSpecifiedDayAndTimeWhenNoTimeNotFound() throws ExecutionException, InterruptedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            PreferredRandomSettings settings = new PreferredRandomSettings.Builder()
                    .time(15)
                    .build();
            AudioFeederFactory factory = new AudioFeederFactory(settings, FeedRequest.builder().build());
            AudioIOFileManager result = factory.process().getCompletableFuture().get();
            System.out.println("File: " + result.getFile().getFullPath());
            assertNotNull(result.getFile());
        }
    }

    @Test
    void PathStrategy_ShouldReturnCorrectTrack_WhenPathExists() throws ExecutionException, InterruptedException {
        try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
            FeedRequest feedRequest = FeedRequest.builder()
                    .path("test/2023/2023-02_February/2023-02-01_Wednesday/TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3")
                    .build();
            AudioFeederFactory factory = new AudioFeederFactory(PreferredRandomSettings.builder().build(), feedRequest);
            AudioIOFileManager result = factory.process().getCompletableFuture().get();
            assertNotNull(result);
            assertEquals("TEST_AUDIOFILE_2023-02-01_Wednesday_12-15-44.mp3", result.getFile().getFileName());
        }
	}

    @Test
    void PathStrategy_ShouldThrowException_WhenPathDoesNotExist() {
        assertThrows(GetFeedException.class, () -> {
            try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
                FeedRequest feedRequest = FeedRequest.builder()
                        .path("test/2023/nonexistent")
                        .build();
                AudioFeederFactory factory = new AudioFeederFactory(PreferredRandomSettings.builder().build(), feedRequest);
                factory.process().getCompletableFuture().get();
            }
        });
    }

    @Test
    void PathStrategy_ShouldThrowException_WhenFileNotFoundInFolder() {
        assertThrows(GetFeedException.class, () -> {
            try (MockedConstruction<File> ignored = FileSystemMock.MockFromJsonFile()) {
                FeedRequest feedRequest = FeedRequest.builder()
                        .path("test/2023/2023-02_February/2023-02-01_Wednesday/nonexistent.mp3")
                        .build();
                AudioFeederFactory factory = new AudioFeederFactory(PreferredRandomSettings.builder().build(), feedRequest);
                factory.process().getCompletableFuture().get();
            }
        });
    }
}
